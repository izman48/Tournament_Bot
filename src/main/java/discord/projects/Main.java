package discord.projects;

import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.*;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
//import net.dv8tion.jda.core.*;

import javax.security.auth.login.LoginException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

public class Main extends ListenerAdapter {
    private static JDA builder;
    private static String botid = "675009649896062996";
    private boolean ready = false;
    private MessageChannel channel;
    private List<String> players = new ArrayList<>();;
    private String tournament_message;
    private int numofplayers = 8;
    private boolean started = false;

    public static void main(String[] args) throws Exception {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File("../TourneyToken")), "UTF8"));
            builder = new JDABuilder(AccountType.BOT).setToken(reader.readLine()).build();
            reader.close();
            builder.addEventListener(new Main());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String author = event.getAuthor().getId();
        Message message = event.getMessage();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        channel = event.getChannel();
        if (event.getAuthor().getId().equals(botid)) {
            message.addReaction("✅").queue();
            tournament_message = message.getId();
        }
        if (event.getChannel().getName().equals("general") && !event.getAuthor().getId().equals(botid)) {
            Guild guild = event.getGuild();
            String content = message.getContentRaw();
            String[] split = content.split("\\s+", -1);
            List<Member> mentioned = message.getMentionedMembers();

            if (!started) {
                if (split[0].equals("!new")) {

                    embedBuilder.setTitle("New Tournament created.", null);
                    embedBuilder.setDescription("Players react to this message");
                    MessageEmbed m = embedBuilder.build();

                    channel.sendMessage(m).queue();
                }
                if (isTarget(split[0], botid) && split[1].toLowerCase().equals("add")){
                    for (Member m : mentioned) {
                        if (!m.getId().equals(botid)) {
                            System.out.println("Player: " + getName(m) + " has been added. Number of players is  : " + (players.size()+1));
                            if (players.size() < numofplayers) {
                                players.add(getName(m));
                                if (players.size() == numofplayers) {
                                    createTournament();
                                }
                            }
                        }

                    }
                    embedBuilder.setTitle("New Tournament created.", null);
                    embedBuilder.setDescription("Players who want to join react to this message");
                    MessageEmbed m = embedBuilder.build();

                    channel.sendMessage(m).queue();
                }
            } else {
                if (split[0].toLowerCase().equals("!restart")) {
                    createTournament();
                }
                if (split[0].toLowerCase().equals("!reset")) {
                    players = new ArrayList<>();
                    started = false;
                    embedBuilder.setTitle("New Tournament created.", null);
                    embedBuilder.setDescription("Players react to this message");
                    MessageEmbed m = embedBuilder.build();

                    channel.sendMessage(m).queue();
                }
            }


            if (isTarget(split[0], botid) && split[1].toLowerCase().equals("remove")){
                for (Member m : mentioned) {
                    if (players.contains(getName(m))) {
                        System.out.println("Player: " + getName(m) + " has been removed. Number of players is  : " + (players.size()+1));
                        players.remove(getName(m));
                    }
                }
            }
//                channel.sendMessage("Thumbs this message").queue();
//                ready = true;

                // if next word is !r (for random) or !c (for captains)
                // enter all players names

                // (random team generator)
                // create random 2s teams.
                // create a bracket
                // give :thumbsup: and :thumbsdown: to vote
                // if more than half thumbs down then recreate the teams

                // directly implement results into ladder



        }
    }

    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (!event.getUserId().equals(botid) && event.getMessageId().equals(tournament_message)) {
            Guild guild = event.getGuild();
            Member member = guild.getMember((event.getUser()));
            if (!players.contains(getName(member)) && players.size() < numofplayers) {
                players.add(getName(member));
                if (players.size() == numofplayers) {
                    createTournament();
                }
            }

        }


    }
    public void onMessageReactionRemove(MessageReactionRemoveEvent event) {
        if (event.getMessageId().equals(tournament_message)) {
            Guild guild = event.getGuild();
            Member member = guild.getMember((event.getUser()));
            players.remove(getName(member));
        }

    }

    private String getName(Member member)
    {
        return member.getEffectiveName().replaceAll(" \".*\" ", " ");
    }

    private boolean isTarget(String arg, String id) {
//        for (String arg : args) {
        if (arg.equals("<@!"+id+">")||arg.equals("<@"+id+">")) {
            return true;
        }
//        }
        return false;
    }
    private void createTournament() {
        //get players
        started = true;
        List<String> currentplayers = new ArrayList<>();
        for (String player : players) {
            currentplayers.add(player);
        }
        System.out.println("Start tournament also players .size() = " + players.size());
        Set<String> team1 = new HashSet<>();
        Set<String> team2 = new HashSet<>();
        Set<String> team3 = new HashSet<>();
        Set<String> team4 = new HashSet<>();
        Random rand = new Random();
        String[] ordered = new String[currentplayers.size()];



        int i = 0;
        while (currentplayers.size() > 0) {
            int r = rand.nextInt(currentplayers.size());
            System.out.println("R is: " + r + " Player is: " + currentplayers.get(r) + " i is: " + i);
            ordered[i] = currentplayers.get(r);
            currentplayers.remove(r);
            i++;
        }
        for (int n = 0; n < ordered.length; n++){
            System.out.println(ordered[n]);
        }
        team1.add(ordered[0]);
        team1.add(ordered[1]);
        team2.add(ordered[2]);
        team2.add(ordered[3]);
        team3.add(ordered[4]);
        team3.add(ordered[5]);
        team4.add(ordered[6]);
        team4.add(ordered[7]);


        // print teams and brackets

        String message = "```\nTeam 1: " + ordered[0] + " and " + ordered[1] + "\n" +
                "Team 2: " + ordered[2] + " and " + ordered[3] + "\n" +
                "Team 3: " + ordered[4] + " and " + ordered[5] + "\n" +
                "Team 4: " + ordered[6] + " and " + ordered[7] + " ```";
        channel.sendMessage(message).queue();
        System.out.println("players.size() is now = " + players.size());
    }
}

