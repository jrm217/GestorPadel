package fin.dam.padel.service;

import com.google.firebase.messaging.*;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class FirebaseMessagingService {

    public String enviarNotificacion(String token, String titulo, String cuerpo) throws InterruptedException, ExecutionException {
        Notification notification = Notification.builder()
                .setTitle(titulo)
                .setBody(cuerpo)
                .build();

        Message message = Message.builder()
                .setToken(token)
                .setNotification(notification)
                .build();

        return FirebaseMessaging.getInstance().sendAsync(message).get();
    }
}
