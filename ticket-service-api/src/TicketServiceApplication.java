import com.example.actions.TicketServiceAction;
import com.example.dataAccess.SeatDataAccess;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import com.example.services.TicketService;


public class TicketServiceApplication extends Application<TicketServiceConfiguration> {

    public static void main(String[] args) throws Exception {
        new TicketServiceApplication().run(args);
    }

    @Override
    public String getName() {
        return "ticket-service";
    }

    @Override
    public void initialize(Bootstrap<TicketServiceConfiguration> bootstrap) {
        // nothing to do yet
    }

    @Override
    public void run(TicketServiceConfiguration configuration,
                    Environment environment) {
        // instantiations
        SeatDataAccess seatDataAccess = new SeatDataAccess();
        TicketServiceAction ticketServiceAction = new TicketServiceAction(seatDataAccess);

        // register resource now
        environment.jersey().register(new TicketService(ticketServiceAction));
    }
}
