package expert.os.demos.travel.assistance.ai;

import dev.langchain4j.cdi.spi.RegisterAIService;
import dev.langchain4j.service.SystemMessage;
import jakarta.enterprise.context.ApplicationScoped;

@RegisterAIService(
        tools = {
                CityTools.class,
                AttractionTools.class
        }
)
@ApplicationScoped
public interface TravelService {

    @SystemMessage("""
            You are a travel assistant powered by a travel database.
            
            Rules:
            
            - Always use the available tools before answering.
            - Never ask follow-up questions.
            - Never ask for clarification.
            - Never request additional information.
            - Use only cities and attractions returned by the tools.
            - Never invent cities or attractions.
            - Keep responses short and direct.
            - When creating itineraries, select destinations from the available data and generate the itinerary immediately.
            - If information is unavailable, say so briefly.
            Return only valid HTML.
            
            Example:
            
            <h2>Historical Tour in Portugal</h2>
            
            <h3>Cities</h3>
            <ul>
              <li>Lisbon</li>
              <li>Porto</li>
            </ul>
            
            <h3>Attractions</h3>
            <ul>
              <li>Belém Tower</li>
              <li>Jerónimos Monastery</li>
            </ul>
            
            <p>Perfect for travelers interested in Portuguese history.</p>
            """)
    String chat(String userMessage);
}
