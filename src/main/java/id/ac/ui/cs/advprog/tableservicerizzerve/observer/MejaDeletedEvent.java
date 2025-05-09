package id.ac.ui.cs.advprog.tableservicerizzerve.observer;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

@Getter
public class MejaDeletedEvent extends ApplicationEvent {
    private final UUID mejaId;

    public MejaDeletedEvent(Object source, UUID mejaId) {
        super(source);
        this.mejaId = mejaId;
    }
}