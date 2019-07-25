package org.lisaac.ldt.editors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.ui.editors.text.FileDocumentProvider;

/**
 * Handle the creation of lisaac document.<br>
 * Attach lisaac partitioning to this documents. 
 */
public class LisaacDocumentProvider extends FileDocumentProvider {
	
	protected IDocument createDocument(Object element) throws CoreException {
		IDocument document = super.createDocument(element);
		if (document != null) {
			IDocumentPartitioner partitioner =
				new FastPartitioner(
					new LisaacPartitionScanner(),
					new String[] {
						LisaacPartitionScanner.LISAAC_DEFAULT,
						LisaacPartitionScanner.LISAAC_COMMENT,
						LisaacPartitionScanner.LISAAC_STRING,
						LisaacPartitionScanner.LISAAC_CHARACTERS,
						LisaacPartitionScanner.LISAAC_EXTERNAL});
			partitioner.connect(document);
			document.setDocumentPartitioner(partitioner);
		}
		return document;
	}
}