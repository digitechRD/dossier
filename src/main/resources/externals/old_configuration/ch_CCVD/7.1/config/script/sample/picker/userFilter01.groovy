import com.digitech.jcorbairs.User

Iterator<User> iter = users.iterator()
while(iter.hasNext()) {
  User user = iter.next()
  if(!user.getName().startsWith("G")) {
    iter.remove()
  }
}
