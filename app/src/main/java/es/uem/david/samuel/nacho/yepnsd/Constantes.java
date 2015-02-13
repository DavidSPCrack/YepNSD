package es.uem.david.samuel.nacho.yepnsd;

/**
 * Created by usuario.apellido on 06/02/2015.
 *
 * @author david.sancho
 */
public class Constantes {

    public static final class CameraActions {
        public static final int TAKE_PHOTO = 0;
        public static final int MAKE_VIDEO = 1;
        public static final int CHOOSE_PHOTO = 2;
        public static final int CHOOSE_VIDEO = 3;
    }

    public static final class Users {
        public static final String FIELD_USERNAME = "username";
        public static final String FIELD_PASSWORD = "password";
        public static final String FIELD_EMAIL = "email";

        public static final int MAX_USERS = 1000;

        public static final String FRIENDS_RELATION = "friendsRelation";
    }
}
