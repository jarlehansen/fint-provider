package no.fint.provider;

import com.github.fakemongo.Fongo;
import com.mongodb.Mongo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

@Profile("local")
@Configuration
public class MongoConfig extends AbstractMongoConfiguration {
    @Value("${fint.audit.mongo.databasename:fint-audit}")
    private String databaseName;

    @Override
    protected String getDatabaseName() {
        return databaseName;
    }

    @Override
    public Mongo mongo() throws Exception {
        return new Fongo(databaseName).getMongo();
    }
}
