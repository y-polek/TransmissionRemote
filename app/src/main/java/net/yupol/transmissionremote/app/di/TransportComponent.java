package net.yupol.transmissionremote.app.di;

import net.yupol.transmissionremote.transport.Transport;

import dagger.Component;

@ServerScope
@Component(
        dependencies = ApplicationComponent.class,
        modules = TransportModule.class
)
public interface TransportComponent {

    Transport transport();
}
