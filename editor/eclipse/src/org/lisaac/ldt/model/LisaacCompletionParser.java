package org.lisaac.ldt.model;


import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.lisaac.ldt.LisaacPlugin;
import org.lisaac.ldt.editors.LisaacEditor;
import org.lisaac.ldt.model.items.ICode;
import org.lisaac.ldt.model.items.ITMPrototype;
import org.lisaac.ldt.model.items.ITMRead;
import org.lisaac.ldt.model.items.Prototype;
import org.lisaac.ldt.model.items.Slot;
import org.lisaac.ldt.model.types.IType;
import org.lisaac.ldt.model.types.ITypeMono;
import org.lisaac.ldt.model.types.TypeGeneric;
import org.lisaac.ldt.model.types.TypeParameter;
import org.lisaac.ldt.model.types.TypeSelf;
import org.lisaac.ldt.outline.OutlineImages;

public class LisaacCompletionParser extends LisaacParser {
	protected static LisaacModel model;

	protected Prototype currentPrototype;
	protected Slot currentSlot;

	protected int endOffset;

	protected TypeGeneric lastGenericType;


	public LisaacCompletionParser(String contents, LisaacModel model) {
		super(null,contents);

		if (model == null) {
			IWorkbenchWindow w = LisaacPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
			if (w != null) {
				IWorkbenchPart part = w.getPartService().getActivePart();
				if (part instanceof LisaacEditor) {
					model = LisaacModel.getModel(((LisaacEditor)part).getProject());
				}
			}
		}
		LisaacCompletionParser.model = model;
		//
		enableErrorReport(false); // turn off error reporting
		//
	} 

	/**
	 * Get the lisaac completions at baseOffset
	 * @param startOffset start offset for completion parsing
	 * @param baseOffset completion offset 
	 * @param proposals list of proposals to be filled  
	 * @throws CoreException
	 */
	public void parseCompletions(int startOffset, int baseOffset, ArrayList<ICompletionProposal> proposals)
	throws CoreException {
		IType type;

		currentPrototype = LisaacModel.getCurrentPrototype();
		currentSlot = currentPrototype.getSlot(startOffset);
		lastGenericType = null;
		endOffset = -1;

		// keyword match
		while (readKeyword()) {
			if (baseOffset != (startOffset+position)) {
				continue;// not last keyword
			}
			String[] keywords = ILisaacModel.keywords;
			for (int i = 0; i < keywords.length; i++) {
				if (keywords[i].startsWith(lastString)) {// length(keyword[i]) >= length(lastString)
					String keywordCompletion = keywords[i].substring(lastString.length());
					proposals.add(new CompletionProposal(keywordCompletion,
							baseOffset, 0, keywordCompletion.length(),
							OutlineImages.KEYWORD, keywords[i], null, ""));
				}
			}
			return;
		}
		setPosition(0);

		// slot match
		ICode code = readExpr();
		if (code != null && currentPrototype != null) {
			type = code.getType(currentSlot, currentPrototype);
			if (type != null) {
				if (source.charAt(position-1) == '.') {
					//
					// slot completion
					//
					if (type instanceof TypeSelf) {
						currentPrototype = findPrototype(((TypeSelf) type).getStaticType());

					} else if (type instanceof TypeParameter && lastGenericType != null) {
						// genericity TypeParameter -> TypeSimple
						int index = currentPrototype.getGenericIndex((TypeParameter) type);
						if (index != -1) { 
							ITypeMono realType = lastGenericType.getGenericElt(index);
							currentPrototype = findPrototype(realType.toString());
						}
					} else {
						currentPrototype = findPrototype(type.toString());
					}
					if (currentPrototype != null) {
						// compute completion result
						currentPrototype.getSlotProposals(proposals, baseOffset, 0);
					}
				} else if (code instanceof ITMPrototype) {
					// partial prototype name
					String prefix = type.toString();
					model.getPathManager().getPathMatch(prefix, proposals, baseOffset);
				}
			} else {
				// partial name, search for matches
				if (code instanceof ITMRead) {
					String prefix = ((ITMRead) code).getName();

					// partial local name
					if (currentSlot != null) {
						currentSlot.getArgumentMatchProposals(prefix, proposals, baseOffset, 0);
						currentSlot.getLocalMatchProposals(prefix, proposals, baseOffset, 0);
					}
					// partial slot name (first keyword)
					currentPrototype.lookupSlotMatch(prefix, proposals, baseOffset, 0);
				}
			}
		} 
	}

	//++ EXPR_MESSAGE -> EXPR_BASE { '.' SEND_MSG }
	protected ICode readExprMessage() {

		ICode result = readExprBase();
		if (result != null) {
			while (readCharacter('.')) {
				if (endOffset != -1 && position == endOffset+1) {
					break;
				}
				ICode lastResult = result;
				result = readSendMsg(result);
				if (result == null) {
					return lastResult;
				}
				// update source of completion
				IType type = lastResult.getType(currentSlot, currentPrototype);
				if (type != null) {
					String stringType = type.toString();
					if (stringType != null && "SELF".compareTo(stringType) != 0) {
						try {
							if (type instanceof TypeParameter && lastGenericType != null) {
								// genericity TypeParameter -> TypeSimple
								int index = currentPrototype.getGenericIndex((TypeParameter) type);
								if (index != -1) { 
									ITypeMono realType = lastGenericType.getGenericElt(index);
									currentPrototype = findPrototype(realType.toString());
								}
							} else {
								currentPrototype = findPrototype(stringType);
							}
						} catch(CoreException e) {
							return null;
						}
						if (currentPrototype == null) {
							return null;
						}
						if (type instanceof TypeGeneric) {
							lastGenericType = (TypeGeneric) type;
						}
						if (result instanceof ITMRead) {
							currentSlot = currentPrototype.lookupSlot(((ITMRead) result).getName());
						} else {
							currentSlot = null;
						}
					}
				}
			}
		}
		return result;
	}


	public Prototype readReceiver(int startOffset, int endOffset, Prototype current) throws CoreException {
		Prototype result=null;
		IType type;

		currentPrototype = current;
		currentSlot = currentPrototype.getSlot(startOffset);
		this.endOffset = endOffset;

		setPosition(startOffset);
		readSpace();

		ICode code = readExpr();
		if (code != null && currentPrototype != null) {
			type = code.getType(currentSlot, currentPrototype);
			if (type != null) {
				//if (! type.equals(TypeSimple.getTypeSelf())) {
				if (type.toString() != null && "SELF".compareTo(type.toString()) != 0) {
					Prototype save = currentPrototype;
					currentPrototype = findPrototype(type.toString());
					if (currentPrototype == null) {
						currentPrototype = save;
					}
				}
				// genericity TypeParameter -> TypeSimple
				if (type instanceof TypeParameter && lastGenericType != null) {
					int index = currentPrototype.getGenericIndex((TypeParameter) type);
					if (index != -1) { 
						ITypeMono realType = lastGenericType.getGenericElt(index);
						currentPrototype = findPrototype(realType.toString());
					}
				}
				result = currentPrototype;	
			}
		}
		return result;
	}

	/**
	 * Find and parse a lisaac prototype according to its name.
	 */
	public static Prototype findPrototype(String prototypeName) throws CoreException {
		IProject project = null;

		if (model == null) {
			IWorkbenchWindow w = LisaacPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
			if (w == null) {
				return null;
			} 
			IWorkbenchPart part = w.getPartService().getActivePart();
			if (part instanceof LisaacEditor) {
				project = ((LisaacEditor)part).getProject();
			}
			if (project != null) {
				model = LisaacModel.getModel(project);
			}
		}
		if (model == null) {
			return null;
		}
		if (project == null) {
			project = model.getProject();
		}
		return model.getPrototype(prototypeName);
	}
}
