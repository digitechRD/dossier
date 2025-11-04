import java.util.Collections;
import java.util.Comparator;

import com.digitech.jcorbairs.Term;

import com.digitech.dossier.common.model.backend.airs.IAttachment;

import java.util.List;
import com.digitech.dossier.common.model.backend.UserCoreContext;
import com.digitech.dossier.common.model.backend.airs.IAttachment;
import org.apache.commons.lang.StringUtils;
// PARAMS
// scriptLogger : log for script
// userContext  : the userContext
// attachmentList : list of attachement to sort

List<IAttachment> theOutput = output;

if( attachmentList != null )
{
	Collections.sort(attachmentList, new Comparator<IAttachment>() {
	public int compare(IAttachment file1, IAttachment file2) 
	{
		String Type1 = file1.getType();
		String Type2 = file2.getType();	
		int res = Type1.compareTo( Type2 );
		return ( res == 0 ? 0 : ( res > 0 ? -1 : 1 ) );
	}
  });

  theOutput.addAll(attachmentsList); 
}