package renamefield.handlers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

public class ContentAssistProcessor implements IContentAssistProcessor {
	 private FileParser fileParser;

	 public ContentAssistProcessor(FileParser fileParser) {
	        this.fileParser = fileParser;
	    }
	
	@Override
	public ICompletionProposal[] computeCompletionProposals(ITextViewer arg0, int arg1) {
		// TODO Auto-generated method stub
		   List<String> suggestions = fileParser.getWordSuggestions();
	        List<ICompletionProposal> proposals = new ArrayList<>();
	        for (String suggestion : suggestions) {
	            proposals.add(new CompletionProposal(suggestion, arg1, 0, suggestion.length()));
	        }
	        return proposals.toArray(new ICompletionProposal[0]);
	}

	@Override
	public IContextInformation[] computeContextInformation(ITextViewer arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public char[] getCompletionProposalAutoActivationCharacters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public char[] getContextInformationAutoActivationCharacters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IContextInformationValidator getContextInformationValidator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getErrorMessage() {
		// TODO Auto-generated method stub
		return null;
	}

}
