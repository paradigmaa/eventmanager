package school.sorokin.eventmanager.users.exception;

public class LoginTakenNameException extends RuntimeException{
    public LoginTakenNameException(String message) {
        super(message);
    }
}
