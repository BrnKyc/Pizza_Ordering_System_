//package whz.pti.eva.security.service.currentUser;
//
//import whz.pti.eva.security.domain.User;
//
//public class CustomUserDetails extends org.springframework.security.core.userdetails.User {
//
//    private Long id;
//
//    public CustomUserDetails(User user) {
//        super(user.getUsername(), user.getPasswordHash(), user.getRole());
//        this.id = user.getId();
//    }
//
//    public Long getId() {
//        return id;
//    }
//}
