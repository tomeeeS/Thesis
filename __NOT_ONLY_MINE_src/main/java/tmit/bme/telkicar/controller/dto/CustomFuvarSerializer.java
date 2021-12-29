package tmit.bme.telkicar.controller.dto;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import tmit.bme.telkicar.domain.user.Felhasznalo;
import tmit.bme.telkicar.domain.advert.Fuvar;

import java.io.IOException;

public class CustomFuvarSerializer extends StdSerializer<Fuvar> {

    protected CustomFuvarSerializer() {
        this(null);
    }

    protected CustomFuvarSerializer(Class<Fuvar> f) {
        super(f);
    }

    @Override
    public void serialize(Fuvar value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();

        Felhasznalo sofor = value.getSofor();
        sofor.setFuvarok(null);
        sofor.setFuvarIgenyek(null);
        sofor.setSajatFuvarok(null);
        sofor.setPassword(null);
        sofor.setRoles(null);

        gen.writeNumberField("id", value.getId());
        gen.writeStringField("indulasiIdo", value.getIndulasiIdo().toString());
        gen.writeStringField("indulasiHely", value.getIndulasiHely());
        gen.writeStringField("uticel", value.getUticel());
        gen.writeStringField("info", value.getInfo());
        gen.writeObjectField("destination", value.getDestination());
        gen.writeObjectField("departure", value.getDeparture());
        gen.writeNumberField("capacity", value.getCapacity());
        gen.writeStringField("rendszam", value.getRendszam());
        gen.writeStringField("autoLeiras", value.getAutoLeiras());
        gen.writeObjectField("statusz", value.getStatusz());
        gen.writeObjectField("sofor", new Felhasznalo(sofor.getId(), null, sofor.getEnabled(),
            sofor.getEmail(), sofor.getKeresztNev(), sofor.getVezetekNev(), sofor.getTel(), sofor.getKredit(),
            null, null, null, null, null, null, null, sofor.getTimeFlexibilityPercentage(), sofor.getDistanceFlexibilityPercentage()));

        gen.writeObjectField("utasok", value.getUtasok().stream().map(u -> {
            u.setSajatFuvarok(null);
            u.setRoles(null);
            u.setFuvarIgenyek(null);
            u.setFuvarok(null);
            return u;
        }));

        gen.writeObjectField("elvallaltIgenyek", value.getElvallaltIgenyek().stream().map(u -> {
            u.setFuvar(null);
            u.setHirdeto(null);
            return u;
        }));

        gen.writeEndObject();
    }
}
