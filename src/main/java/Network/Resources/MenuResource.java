package Network.Resources;

import Network.ModelGateway;
import com.google.gson.Gson;
import core.user.User;
import org.json.JSONObject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Path("menu")
public class MenuResource {

    @POST
    @Path("createUser")
    @Produces(MediaType.TEXT_PLAIN)
    public Response createUser(String username) {

        JSONObject obj = new JSONObject(username);
        String name = obj.getString("name");

        Response res;
        if (!ModelGateway.getController().registerNewPlayer(name)){
            throw new WebApplicationException(400);
        }
        String str = "User Created Successfully";
        res = Response.ok(str).build();
        return res;
    }

    @PUT
    @Path("myGames")
    @Produces(MediaType.APPLICATION_JSON)
    public String myGamesInProgress(String username){
        JSONObject obj = new JSONObject(username);
        String name = obj.getString("name");

        GameInfoList games = new GameInfoList();

        String myInProgressGames = ModelGateway.getController().seeMyGames(name);
        String[] splitStr = myInProgressGames.split("\n");

        if (splitStr.length == 0){
            throw new WebApplicationException(400);
        }

        for (int i = 0; i < splitStr.length; i++){
            String[] splitRow = splitStr[i].split("\\s+");
            int id = Integer.parseInt(splitRow[0]);
            String redPlayer = splitRow[1];
            String bluePlayer = splitRow[2];
            GameInfo newObj = new GameInfo(id, redPlayer, bluePlayer);
            games.addGameInfo(newObj);
        }

        Gson gson = new Gson();
        return gson.toJson(games);
    }

    @GET
    @Path("inProgress")
    @Produces(MediaType.APPLICATION_JSON)
    public String getGamesInProgress() {
        GameInfoList games = new GameInfoList();
        String inProgressGames = ModelGateway.getController().seeInProgressGames();
        String[] splitStr = inProgressGames.split("\n");

        for(int i = 0; i < splitStr.length; i++) {
            String[] splitRow = splitStr[i].split("\\s+");
            int id = Integer.parseInt(splitRow[0]);
            String redP = splitRow[1];
            String blueP = splitRow[2];
            GameInfo newObject = new GameInfo(id, redP, blueP);
            games.addGameInfo(newObject);
        }
        Gson gson = new Gson();
        return gson.toJson(games);
    }

    @GET
    @Path("completed")
    @Produces(MediaType.APPLICATION_JSON)
    public String getFinishedGames() {
        GameInfoList games = new GameInfoList();
        String inProgressGames = ModelGateway.getController().seeFinishedGames();
        if (inProgressGames.isEmpty()){
            return "";
        }
        String[] splitStr = inProgressGames.split("\n");
        for(int i = 0; i < splitStr.length; i++) {
            String[] splitRow = splitStr[i].split("\\s+");
            int id = Integer.parseInt(splitRow[0]);
            String redP = splitRow[1];
            String blueP = splitRow[2];
            GameInfo newObject = new GameInfo(id, redP, blueP);
            games.addGameInfo(newObject);
        }

        Gson gson = new Gson();
        return gson.toJson(games);
    }

    @GET
    @Path("leaderboard")
    public String getLeaderboard() {

        LeaderBoardInfo leaderboard = new LeaderBoardInfo();
        Map<String, User> sortedUsers = ModelGateway.getController().getSortedByScore();

        for( String key : sortedUsers.keySet()){
            User row = sortedUsers.get(key);
            UserInfoRow newUser = new UserInfoRow(row.getName(), row.getScore(), row.getWins(),
                                    row.getLosses(), row.getTies());
            leaderboard.addUserInfo(newUser);
        }

        Gson gson = new Gson();
        return gson.toJson(leaderboard);
    }

    public class LeaderBoardInfo {
        private List<UserInfoRow> leaderboardRows = new ArrayList<>();

        public void addUserInfo(UserInfoRow newObject){
            leaderboardRows.add(newObject);
        }

        public List<UserInfoRow> getRows(){
            return leaderboardRows;
        }
    }

    public class UserInfoRow {
        private String name;
        private int score;
        private int wins;
        private int losses;
        private int ties;

        UserInfoRow(String newName, int newScore, int newWins, int newLosses, int newTies){
            name = newName;
            score = newScore;
            wins = newWins;
            losses = newLosses;
            ties = newTies;
        }

        public String getName() { return name; }
        public int getScore() { return score; }
        public int getWins() { return wins; }
        public int getLosses() { return losses; }
        public int getTies() { return ties; }
    }

    public class GameInfo {
        private int id;
        private String redPlayer;
        private String bluePlayer;

        GameInfo(int newID, String newRed, String newBlue) {
            id = newID;
            redPlayer = newRed;
            bluePlayer = newBlue;
        }

        public int getId() { return id; }
        public String getRedPlayer() { return redPlayer; }
        public String getBluePlayer() { return bluePlayer; }
    }

    public class GameInfoList {
        private List<GameInfo> gameInfos = new ArrayList<>();

        public void addGameInfo(GameInfo game) { gameInfos.add(game); }

        public List<GameInfo> getGameInfos() { return gameInfos; }
    }

}
