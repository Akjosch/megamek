package megamek.client.commands;

import megamek.client.Client;

/**
 * A command that exist purely for the accessibility UI so that commands can be made as the default rather than chat.
 */
public class ChatCommand extends ClientCommand {

    public ChatCommand(Client client) {
        super(client, "say", "say <message>, sends message to chat.");
    }

    @Override
    public String run(String[] args) {
        //rejoin the string but cut off the say at the beginning.
        String str = String.join(" ", args).substring(4);
        client.sendChat(str);

        return str;
    }
}
