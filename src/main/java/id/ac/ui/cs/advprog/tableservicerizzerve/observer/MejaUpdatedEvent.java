package id.ac.ui.cs.advprog.tableservicerizzerve.observer;

import id.ac.ui.cs.advprog.tableservicerizzerve.model.Meja;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class MejaUpdatedEvent extends ApplicationEvent {
    private final Meja meja;
    public MejaUpdatedEvent(Object source, Meja meja) {
    }
}