package de.timesnake.extension.discord.main;

import de.timesnake.extension.discord.wrapper.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.requests.RestAction;

import java.util.LinkedList;
import java.util.List;

public class TimeSnakeGuild {

    private static TimeSnakeGuild getInstance() {
        if (instance == null) {
            instance = new TimeSnakeGuild();
        }
        return instance;
    }

    // Static function declaration /////////////////////////////////////////////////////////////////////////////////////
    public static JDA getApi() {
        return getInstance()._getApi();
    }

    public static long getGuildID() {
        return getInstance()._getGuildID();
    }

    public static void initialize(JDA api, long guildID) {
        getInstance()._initialize(api, guildID);
    }

    public static List<ExGuildChannel> getChannels() {
        return getInstance()._getChannels(true);
    }

    public static List<ExGuildChannel> getChannels(boolean showHidden) {
        return getInstance()._getChannels(showHidden);
    }

    public static ExCategory createCategory(String name) {
        return getInstance()._createCategory(name, null);
    }

    public static ExCategory createCategory(String name, Integer pos) {
        return getInstance()._createCategory(name, pos);
    }

    public static ExVoiceChannel createVoiceChannel(String name) {
        return getInstance()._createVoiceChannel(name, null);
    }

    public static ExVoiceChannel createVoiceChannel(String name, ExCategory parent) {
        return getInstance()._createVoiceChannel(name, parent);
    }

    public static boolean moveVoiceMember(ExMember member, ExVoiceChannel vc) {
        return getInstance()._moveVoiceMember(member, vc);
    }

    public static boolean moveVoiceMember(ExMember member, ExVoiceChannel vc, boolean async) {
        return getInstance()._moveVoiceMember(member, vc, async);
    }

    public static ExMember getMember(ExUser user) {
        return getInstance()._getMember(user);
    }

    public static List<ExCategory> getCategoriesByName(String name) {
        return getInstance()._getCategoriesByName(name, false);
    }

    public static List<ExCategory> getCategoriesByName(String name, boolean ignoreCase) {
        return getInstance()._getCategoriesByName(name, ignoreCase);
    }

    protected static TimeSnakeGuild instance;
    private JDA api;
    private long guildID;

    protected TimeSnakeGuild() {

    }

    private Guild getGuild() {
        return api.getGuildById(guildID);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // protected implementation ////////////////////////////////////////////////////////////////////////////////////////
    protected JDA _getApi() {
        return api;
    }

    protected long _getGuildID() {
        return guildID;
    }

    protected void _initialize(JDA api, long guildID) {
        this.api = api;
        this.guildID = guildID;
    }

    protected List<ExGuildChannel> _getChannels(boolean showHidden) {
        List<GuildChannel> channels = getGuild().getChannels(showHidden);
        List<ExGuildChannel> res = new LinkedList<>();
        for (GuildChannel c : channels) {
            res.add(new ExGuildChannel(c));
        }
        return res;
    }

    protected ExCategory _createCategory(String name, Integer pos) {
        Category c = getGuild().createCategory(name).setPosition(pos).complete();
        return new ExCategory(c);
    }

    protected ExVoiceChannel _createVoiceChannel(String name, ExCategory parent) {
        VoiceChannel vc = getGuild().createVoiceChannel(name, getApi().getCategoryById(parent.getID())).complete();
        return new ExVoiceChannel(vc);
    }

    protected boolean _moveVoiceMember(ExMember member, ExVoiceChannel vc) {
        if (!member.exists() || !member.isInVoiceChannel()) return false;

        getGuild().moveVoiceMember(getGuild().getMemberById(member.getID()), getGuild().getVoiceChannelById(vc.getID())).queue();
        return true;
    }

    protected boolean _moveVoiceMember(ExMember member, ExVoiceChannel vc, boolean async) {
        if (!member.exists() || !member.isInVoiceChannel()) return false;

        RestAction<Void> action = getGuild().moveVoiceMember(getGuild().getMemberById(member.getID()), getGuild().getVoiceChannelById(vc.getID()));
        if (async) {
            action.queue();
        } else {
            action.complete();
        }
        return true;
    }

    protected ExMember _getMember(ExUser user) {
        if (!user.exists()) return null;
        return new ExMember(getGuild().getMember(api.getUserById(user.getID())));
    }

    protected List<ExCategory> _getCategoriesByName(String name, boolean ignoreCase) {
        List<Category> categories = getGuild().getCategoriesByName(name, ignoreCase);
        List<ExCategory> res = new LinkedList<>();
        for (Category c : categories) {
            res.add(new ExCategory(c));
        }
        return res;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


}
