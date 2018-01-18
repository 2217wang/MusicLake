package com.cyl.musiclake.api.xiami;

import com.cyl.musiclake.api.ApiManager;
import com.cyl.musiclake.data.model.Music;
import com.cyl.musiclake.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;

/**
 * Created by yonglong on 2018/1/15.
 */

public class XiamiServiceImpl {

    private static final String TAG = "XiamiServiceImpl";

    /**
     * 搜索虾米音乐
     *
     * @param key   关键字
     * @param limit
     * @param page
     * @return
     */
    @SuppressWarnings({"unchecked", "varargs"})
    public static Observable<List<Music>> search(String key, int limit, int page) {
        Map<String, String> params = new HashMap<>();
        params.put("v", "2.0"); //page
        params.put("page", String.valueOf(page)); //page
        params.put("limit", String.valueOf(limit));//limit
        params.put("key", key);// key
        params.put("r", "search/songs");
        params.put("app_key", "1");
        params.put("format", "json");
        return ApiManager.getInstance().apiService.searchByXiaMi(Constants.BASE_URL_XIAMI_MUSIC, params)
                .flatMap(xiaMiModel -> {
                    List<Music> musicList = new ArrayList<>();
                    List<XiamiModel.DataBean.SongsBean> songs = xiaMiModel.getData().getSongs();
                    for (int i = 0; i < songs.size(); i++) {
                        XiamiModel.DataBean.SongsBean song = songs.get(i);
                        Music music = new Music();
                        music.setType(Music.Type.XIAMI);
                        music.setOnline(true);
                        music.setId(String.valueOf(song.getSong_id()));
                        music.setTitle(song.getSong_name());
                        music.setArtist(song.getArtist_name());
                        music.setArtistId(song.getArtist_id());
                        music.setAlbum(song.getAlbum_name());
                        music.setAlbumId(song.getAlbum_id());
                        music.setUri(song.getListen_file());

                        String cover = song.getAlbum_logo() + "@1e_1c_0i_1o_100Q_250w_250h";
                        String coverBig = song.getAlbum_logo() + "@1e_1c_0i_1o_100Q_400w_400h";
                        String coverSmall = song.getAlbum_logo() + "@1e_1c_0i_1o_100Q_150w_150h";
                        music.setCoverUri(cover);
                        music.setCoverBig(coverBig);
                        music.setCoverSmall(coverSmall);
                        music.setLrcPath(song.getLyric());
                        musicList.add(music);
                    }
                    return Observable.fromArray(musicList);
                });
    }


    @SuppressWarnings({"unchecked", "varargs"})
    public static Observable<String> getXimaiLyric(String lyricPath) {
        return ApiManager.getInstance().apiService.getXiamiLyric(lyricPath)
                .flatMap(xiaMiLyricInfo -> {
                    String lyric = xiaMiLyricInfo.string();
                    return Observable.fromArray(lyric);
                });
    }
}
