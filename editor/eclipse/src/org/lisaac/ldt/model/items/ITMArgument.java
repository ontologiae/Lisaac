package org.lisaac.ldt.model.items;

import java.util.ArrayList;

import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.lisaac.ldt.editors.ColorManager;
import org.lisaac.ldt.model.Position;
import org.lisaac.ldt.model.types.IType;
import org.lisaac.ldt.model.types.ITypeMono;
import org.lisaac.ldt.outline.OutlineImages;

public class ITMArgument implements IArgument {
	
	protected String name;
	protected ITypeMono type;
	
	protected Position position;
	
	public ITMArgument(String name, ITypeMono type, Position position) {
		this.name = name;
		this.type = type;
		this.position = position;
	}

	public String getName() {
		return name;
	}

	public IType getType() {
		return type;
	}
	
	public boolean hasName(String word) {
		return name.compareTo(word) == 0;
	}
	
	public void printIn(StringBuffer buffer) {
		buffer.append(name);
		buffer.append(':');
		buffer.append(type);
	}
	
	public void styledPrintIn(StyledString buffer) {
		ColorManager colors = ColorManager.getDefault();
		buffer.append(name, colors.getVariableStyler());
		buffer.append(':');
		buffer.append(type.toString(), colors.getPrototypeStyler());
	}
	
	public String getHoverInformation() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<I>Argument</I> : <b>");
		buffer.append(name);
		buffer.append("</b> <g> : ");
		buffer.append(type.toString());
		buffer.append("</g>");
		
		return buffer.toString();
	}

	public Position getPosition() {
		return position;
	}

	public String match(String n) {
		if (name.startsWith(n)) {
			return name;
		}
		return null;
	}

	public void getMatchProposals(String n,
			ArrayList<ICompletionProposal> matchList, int offset, int length) {
		
		String match = match(n);
		if (match != null && Slot.checkUnicity(matchList, match)) {
			Image image = OutlineImages.PRIVATE_NONSHARED;// TODO new image for args
			
			String partialMatch = match.substring(n.length());
			matchList.add(new CompletionProposal(partialMatch, offset, length,
					partialMatch.length(), image, match, null,
					null));
		}
	}
}
