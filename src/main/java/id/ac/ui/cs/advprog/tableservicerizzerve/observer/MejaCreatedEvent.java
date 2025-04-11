package id.ac.ui.cs.advprog.tableservicerizzerve.observer;

import id.ac.ui.cs.advprog.tableservicerizzerve.model.Meja;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class MejaCreatedEvent extends ApplicationEvent {

    private final Meja meja;

    public MejaCreatedEvent(Object source, Meja meja) {
        super(source);
        this.meja = meja;
    }
}