package org.lisaac.ldt.model.items;

import java.util.ArrayList;

import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.lisaac.ldt.editors.ColorManager;
import org.lisaac.ldt.model.Position;
import org.lisaac.ldt.model.types.IType;
import org.lisaac.ldt.model.types.TypeMulti;
import org.lisaac.ldt.outline.OutlineImages;

public class ITMArgs implements IArgument {

	protected String[] name;
	protected TypeMulti type;

	protected Position position;
	
	public ITMArgs(String[] name, TypeMulti type, Position position) {
		this.name = name;
		this.type = type;
		this.position = position;
	}

	public String getName() {
		StringBuffer buffer = new StringBuffer("(");
		for (int i=0; i<name.length; i++) {
			buffer.append(name[i]);
			
			if (i != name.length-1) {
				buffer.append(", ");
			}
		}
		buffer.append(")");
		return buffer.toString();
	}

	private int lastIndexOf(String word) {
		for (int i=0; i<name.length; i++) {
			if (name[i].compareTo(word) == 0) {
				return i;
			}
		}
		return -1;
	}

	public boolean hasName(String word) {
		return lastIndexOf(word) != -1;
	}
	
	public IType getType() {
		return null;// FIXME list arg type
	}
	
	public IType getArgType(String name) {
		int index = lastIndexOf(name);
		if (index != -1) {
			return type.getSubType(index);
		}
		return null;
	}
	
	public void printIn(StringBuffer buffer) {
		buffer.append("(");
		for (int i=0; i<name.length; i++) {
			IType subType = type.getSubType(i);
			buffer.append(name[i]);
			buffer.append(" : ");
			buffer.append(subType);
			
			if (i != name.length-1) {
				buffer.append(", ");
			}
		}
		buffer.append(")");
	}

	public void styledPrintIn(StyledString buffer) {
		ColorManager colors = ColorManager.getDefault();
		buffer.append("(");
		for (int i=0; i<name.length; i++) {
			IType subType = type.getSubType(i);
			buffer.append(name[i], colors.getVariableStyler());
			buffer.append(" : ");
			buffer.append(subType.toString(), colors.getPrototypeStyler());
			
			if (i != name.length-1) {
				buffer.append(", ");
			}
		}
		buffer.append(")");
	}
	
	public String getHoverInformation() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<I>Arguments</I> : ");
		buffer.append("(");
		for (int i=0; i<name.length; i++) {
			IType subType = type.getSubType(i);
			buffer.append("<b>"+name[i]+"</b>");
			buffer.append(" : ");
			buffer.append("<g>"+subType+"</g>");
			
			if (i != name.length-1) {
				buffer.append(", ");
			}
		}
		buffer.append(")");
		
		return buffer.toString();
	}

	public Position getPosition() {
		return position;
	}

	public void getMatchProposals(String n,
			ArrayList<ICompletionProposal> matchList, int offset, int length) {
		
		for (int i=0; i<name.length; i++) {
			if (name[i].startsWith(n)) {
				String match = name[i];
				if (Slot.checkUnicity(matchList, match)) {
					Image image = OutlineImages.PRIVATE_NONSHARED;// TODO new image for args
					
					String partialMatch = match.substring(n.length());
					matchList.add(new CompletionProposal(partialMatch, offset, length,
							partialMatch.length(), image, match, null,
							null));
				}
			}
		}	
	}
}
	