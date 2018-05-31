package com.boer.delos.model;

import java.util.List;

/**
 * Created by sunzhibin on 2017/8/15.
 * 背景音乐
 */

public class MusicResult extends BaseResult {


    /**
     * response : {"songList":[{"duration":261277,"index":0,"title":"演员","file_name":"/mnt/internal_sd/netease/cloudmusic/Music/薛之谦-演员.mp3","artist":"薛之谦"},{"duration":289411,"index":1,"title":"爱很简单 - live","file_name":"/mnt/internal_sd/netease/cloudmusic/Music/张学友-爱很简单-live.mp3","artist":"张学友"},{"duration":257306,"index":2,"title":"心碎了无痕","file_name":"/mnt/internal_sd/netease/cloudmusic/Music/张学友-心碎了无痕.mp3","artist":"张学友"},{"duration":220264,"index":3,"title":"星晴 - live","file_name":"/mnt/internal_sd/netease/cloudmusic/Music/张学友-星晴-live.mp3","artist":"张学友"},{"duration":230949,"index":4,"title":"小城大事","file_name":"/mnt/internal_sd/netease/cloudmusic/Music/张学友-小城大事.mp3","artist":"张学友"},{"duration":228493,"index":5,"title":"父亲写的散文诗(时光版)","file_name":"/mnt/internal_sd/netease/cloudmusic/Music/许飞-父亲写的散文诗(时光版).mp3","artist":"许飞"},{"duration":207778,"index":6,"title":"味道 - live","file_name":"/mnt/internal_sd/netease/cloudmusic/Music/张学友-味道-live.mp3","artist":"张学友"},{"duration":200046,"index":7,"title":"刚好遇见你","file_name":"/mnt/internal_sd/netease/cloudmusic/Music/李玉刚-刚好遇见你.mp3","artist":"李玉刚"},{"duration":328464,"index":8,"title":"成都","file_name":"/mnt/internal_sd/netease/cloudmusic/Music/赵雷-成都.mp3","artist":"赵雷"},{"duration":221336,"index":9,"title":"Just the Way You Are","file_name":"/mnt/internal_sd/netease/cloudmusic/Music/BrunoMars-JusttheWayYouAre.mp3","artist":"Bruno Mars"},{"duration":231654,"index":10,"title":"幸福的两口子","file_name":"/mnt/internal_sd/netease/cloudmusic/Music/庞龙-幸福的两口子.mp3","artist":"庞龙"},{"duration":281548,"index":11,"title":"爱如空气","file_name":"/mnt/internal_sd/netease/cloudmusic/Music/孙俪-爱如空气.mp3","artist":"孙俪"},{"duration":210181,"index":12,"title":"一万个舍不得","file_name":"/mnt/internal_sd/netease/cloudmusic/Music/庄心妍-一万个舍不得.mp3","artist":"庄心妍"},{"duration":217992,"index":13,"title":"Trouble Is A Friend","file_name":"/mnt/internal_sd/netease/cloudmusic/Music/Lenka-TroubleIsAFriend.mp3","artist":"Lenka"},{"duration":279954,"index":14,"title":"北京东路的日子","file_name":"/mnt/internal_sd/netease/cloudmusic/Music/刘千楚徐逸昊鲁天舒姜玮珉胡梦原张鎏依梁竞元游彧涵金书援许一璇汪源张夙西-北京东路的日子.mp3","artist":"刘千楚/徐逸昊/鲁天舒/姜玮珉/胡梦原/张鎏依/梁竞元/游彧涵/金书援/许一璇/汪源/张夙西"},{"duration":246596,"index":15,"title":"Tell Me Goodbye","file_name":"/mnt/internal_sd/netease/cloudmusic/Music/BIGBANG-TellMeGoodbye.mp3","artist":"BIGBANG"},{"duration":274312,"index":16,"title":"不说再见(许飞版)","file_name":"/mnt/internal_sd/netease/cloudmusic/Music/许飞-不说再见(许飞版).mp3","artist":"许飞"},{"duration":267990,"index":17,"title":"IF YOU -JP Ver.-","file_name":"/mnt/internal_sd/netease/cloudmusic/Music/BIGBANG-IFYOU-JPVer.-.mp3","artist":"BIGBANG"},{"duration":261956,"index":18,"title":"Far Away From Home","file_name":"/mnt/internal_sd/netease/cloudmusic/Music/GrooveCoverage-FarAwayFromHome.mp3","artist":"Groove Coverage"},{"duration":234266,"index":19,"title":"Blue","file_name":"/mnt/internal_sd/netease/cloudmusic/Music/BIGBANG-Blue.mp3","artist":"BIGBANG"},{"duration":228336,"index":20,"title":"LIE","file_name":"/mnt/internal_sd/netease/cloudmusic/Music/BIGBANG-LIE.mp3","artist":"BIGBANG"}]}
     */

    private ResponseBean response;

    public ResponseBean getResponse() {
        return response;
    }

    public void setResponse(ResponseBean response) {
        this.response = response;
    }

    public static class ResponseBean {
        private List<MusicBean> songList;

        public List<MusicBean> getSongList() {
            return songList;
        }

        public void setSongList(List<MusicBean> songList) {
            this.songList = songList;
        }

        public static class MusicBean {
            /**
             * duration : 261277
             * index : 0
             * title : 演员
             * file_name : /mnt/internal_sd/netease/cloudmusic/Music/薛之谦-演员.mp3
             * artist : 薛之谦
             */

            private int duration;
            private int index;
            private String title;
            private String file_name;
            private String artist;

            public int getDuration() {
                return duration;
            }

            public void setDuration(int duration) {
                this.duration = duration;
            }

            public int getIndex() {
                return index;
            }

            public void setIndex(int index) {
                this.index = index;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getFile_name() {
                return file_name;
            }

            public void setFile_name(String file_name) {
                this.file_name = file_name;
            }

            public String getArtist() {
                return artist;
            }

            public void setArtist(String artist) {
                this.artist = artist;
            }
        }
    }
}
