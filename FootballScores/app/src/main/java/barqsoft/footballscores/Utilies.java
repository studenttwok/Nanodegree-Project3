package barqsoft.footballscores;

import android.content.Context;

/**
 * Created by yehya khaled on 3/3/2015.
 */
public class Utilies
{
    public static final int SERIE_A = 357;
    public static final int PREMIER_LEGAUE = 354;
    public static final int CHAMPIONS_LEAGUE = 362;
    public static final int PRIMERA_DIVISION = 358;
    public static final int BUNDESLIGA = 351;
    public static String getLeague(Context context, int league_num)
    {
        switch (league_num)
        {
            case SERIE_A : return context.getString(R.string.league_name_seria_a);
            case PREMIER_LEGAUE : return context.getString(R.string.league_name_premier_league);
            case CHAMPIONS_LEAGUE : return context.getString(R.string.league_name_champions_league);
            case PRIMERA_DIVISION : return context.getString(R.string.league_name_primera_division);
            case BUNDESLIGA : return context.getString(R.string.league_name_bundesliga);
            default: return context.getString(R.string.league_name_unknown);
        }
    }
    public static String getMatchDay(Context context, int match_day,int league_num)
    {
        if(league_num == CHAMPIONS_LEAGUE)
        {
            if (match_day <= 6)
            {
                return context.getString(R.string.matchday_six);
            }
            else if(match_day == 7 || match_day == 8)
            {
                return context.getString(R.string.matchday_seven);
            }
            else if(match_day == 9 || match_day == 10)
            {
                return context.getString(R.string.matchday_nine);
            }
            else if(match_day == 11 || match_day == 12)
            {
                return context.getString(R.string.matchday_eleven);
            }
            else
            {
                return context.getString(R.string.matchday_final);
            }
        }
        else
        {
            return context.getString(R.string.matchday_str, String.valueOf(match_day));
        }
    }

    public static String getScores(Context context, int home_goals,int awaygoals)
    {
        if(home_goals < 0 || awaygoals < 0)
        {
            return context.getString(R.string.spaceHypen);
        }
        else
        {
            return String.valueOf(home_goals) + context.getString(R.string.spaceHypen) + String.valueOf(awaygoals);
        }
    }

    public static int getTeamCrestByTeamName(Context context, String teamname)
    {
        if (teamname==null){return R.drawable.no_icon;}

        if (teamname.equals(context.getString(R.string.teamname_arsenal))) {
            return R.drawable.arsenal;
        } else if (teamname.equals(context.getString(R.string.teamname_manchester_united))) {
            return R.drawable.manchester_united;
        } else if (teamname.equals(context.getString(R.string.teamname_swansea_city_afc))) {
            return R.drawable.swansea_city_afc;
        } else if (teamname.equals(context.getString(R.string.teamname_leicester_city_fc))) {
            return R.drawable.leicester_city_fc_hd_logo;
        } else if (teamname.equals(context.getString(R.string.teamname_everton_fc))) {
            return R.drawable.everton_fc_logo1;
        } else if (teamname.equals(context.getString(R.string.teamname_west_ham))) {
            return R.drawable.west_ham;
        } else if (teamname.equals(context.getString(R.string.teamname_tottenham_hotspur))) {
            return R.drawable.tottenham_hotspur;
        } else if (teamname.equals(context.getString(R.string.teamname_west_bromwich_albion))) {
            return R.drawable.west_bromwich_albion_hd_logo;
        } else if (teamname.equals(context.getString(R.string.teamname_sunderland))) {
            return R.drawable.sunderland;
        } else if (teamname.equals(context.getString(R.string.teamname_stoke_city))) {
            return R.drawable.stoke_city;
        } else {
            return R.drawable.no_icon;
        }

    }
}
