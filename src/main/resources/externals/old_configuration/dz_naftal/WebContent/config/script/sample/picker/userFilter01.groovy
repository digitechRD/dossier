import java.lang.*;
import java.util.*;
import org.apache.commons.lang.*;
import com.digitech.jcorbairs.*;

Iterator<User> iter = users.iterator();
while(iter.hasNext()) {
    User user = iter.next();
    if (!user.getName().startsWith("G")) {
      iter.remove();
    }
}
