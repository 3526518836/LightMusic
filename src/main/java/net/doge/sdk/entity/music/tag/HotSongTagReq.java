package net.doge.sdk.entity.music.tag;

import cn.hutool.http.Method;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.async.GlobalExecutors;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.Tags;
import net.doge.sdk.common.opt.kg.KugouReqOptEnum;
import net.doge.sdk.common.opt.kg.KugouReqOptsBuilder;
import net.doge.sdk.common.opt.nc.NeteaseReqOptEnum;
import net.doge.sdk.common.opt.nc.NeteaseReqOptsBuilder;
import net.doge.util.common.JsonUtil;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class HotSongTagReq {
    private static HotSongTagReq instance;

    private HotSongTagReq() {
    }

    public static HotSongTagReq getInstance() {
        if (instance == null) instance = new HotSongTagReq();
        return instance;
    }

    // 曲风 API
    private final String STYLE_API = "https://music.163.com/api/tag/list/get";
    // 主题音乐标签 API (酷狗)
    private final String THEME_SONG_TAG_API = "/everydayrec.service/v1/mul_theme_category_recommend";

    /**
     * 加载飙升歌曲标签
     *
     * @return
     */
    public void initHotSongTag() {
        // 网易云 酷狗 酷狗 音乐磁场 咕咕咕音乐
        Tags.hotSongTag.put("默认", new String[]{"", "1", "", "index", "index"});

        // 酷狗
        Tags.hotSongTag.put("精选好歌随心听", new String[]{"", "1", "", "", ""});
        Tags.hotSongTag.put("经典怀旧金曲", new String[]{"", "2", "", "", ""});
        Tags.hotSongTag.put("热门好歌精选", new String[]{"", "3", "", "", ""});
        Tags.hotSongTag.put("小众宝藏佳作", new String[]{"", "4", "", "", ""});
        Tags.hotSongTag.put("未知", new String[]{"", "5", "", "", ""});
        Tags.hotSongTag.put("Vip 专属推荐", new String[]{"", "6", "", "", ""});

        // 音乐磁场
        Tags.hotSongTag.put("热门", new String[]{"", "", "", "index-0-2", "index-0-hot"});
        Tags.hotSongTag.put("月榜", new String[]{"", "", "", "index-0-3", ""});
        Tags.hotSongTag.put("周榜", new String[]{"", "", "", "index-0-4", ""});
        Tags.hotSongTag.put("日榜", new String[]{"", "", "", "index-0-5", ""});
        Tags.hotSongTag.put("华语", new String[]{"", "", "", "forum-1", "forum-1"});
        Tags.hotSongTag.put("日韩", new String[]{"", "", "", "forum-15", "forum-7"});
        Tags.hotSongTag.put("欧美", new String[]{"", "", "", "forum-10", "forum-3"});
        Tags.hotSongTag.put("Remix", new String[]{"", "", "", "forum-11", ""});
        Tags.hotSongTag.put("纯音乐", new String[]{"", "", "", "forum-12", "forum-6"});
        Tags.hotSongTag.put("异次元", new String[]{"", "", "", "forum-13", ""});
        Tags.hotSongTag.put("特供", new String[]{"", "", "", "forum-17", ""});
        Tags.hotSongTag.put("百科", new String[]{"", "", "", "forum-18", ""});
        Tags.hotSongTag.put("站务", new String[]{"", "", "", "forum-9", ""});

        // 咕咕咕音乐
        Tags.hotSongTag.put("音乐分享区", new String[]{"", "", "", "", "forum-12"});
        Tags.hotSongTag.put("伤感", new String[]{"", "", "", "", "forum-8"});
        Tags.hotSongTag.put("粤语", new String[]{"", "", "", "", "forum-2"});
        Tags.hotSongTag.put("青春", new String[]{"", "", "", "", "forum-5"});
        Tags.hotSongTag.put("分享", new String[]{"", "", "", "", "forum-11"});
        Tags.hotSongTag.put("温柔男友音", new String[]{"", "", "", "", "forum-10"});
        Tags.hotSongTag.put("DJ", new String[]{"", "", "", "", "forum-9"});

        final int c = 5;
        // 网易云曲风
        Runnable initHotSongTag = () -> {
            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weapi();
            String tagBody = SdkCommon.ncRequest(Method.POST, STYLE_API, "{}", options)
                    .executeAsync()
                    .body();
            JSONObject tagJson = JSONObject.parseObject(tagBody);
            JSONArray tags = tagJson.getJSONArray("data");
            for (int i = 0, len = tags.size(); i < len; i++) {
                JSONObject tag = tags.getJSONObject(i);

                String name = tag.getString("tagName");
                String id = tag.getString("tagId");

                if (!Tags.hotSongTag.containsKey(name)) Tags.hotSongTag.put(name, new String[c]);
                Tags.hotSongTag.get(name)[0] = id;
                // 子标签
                JSONArray subTags = tag.getJSONArray("childrenTags");
                if (JsonUtil.isEmpty(subTags)) continue;
                for (int j = 0, s = subTags.size(); j < s; j++) {
                    JSONObject subTag = subTags.getJSONObject(j);

                    String subName = subTag.getString("tagName");
                    String subId = subTag.getString("tagId");

                    if (!Tags.hotSongTag.containsKey(subName)) Tags.hotSongTag.put(subName, new String[c]);
                    Tags.hotSongTag.get(subName)[0] = subId;
                    // 孙子标签
                    JSONArray ssTags = subTag.getJSONArray("childrenTags");
                    if (JsonUtil.isEmpty(ssTags)) continue;
                    for (int k = 0, l = ssTags.size(); k < l; k++) {
                        JSONObject ssTag = ssTags.getJSONObject(k);

                        String ssName = ssTag.getString("tagName");
                        String ssId = ssTag.getString("tagId");

                        if (!Tags.hotSongTag.containsKey(ssName)) Tags.hotSongTag.put(ssName, new String[c]);
                        Tags.hotSongTag.get(ssName)[0] = ssId;
                    }
                }
            }
        };

        // 酷狗主题音乐标签
        Runnable initThemeSongTagKg = () -> {
            Map<KugouReqOptEnum, Object> options = KugouReqOptsBuilder.androidPost(THEME_SONG_TAG_API);
            long ct = System.currentTimeMillis() / 1000;
            String dat = String.format("{\"platform\":\"android\",\"clienttime\":%s,\"userid\":0,\"module_id\":508}", ct);
            String tagBody = SdkCommon.kgRequest(null, dat, options)
                    .executeAsync()
                    .body();
            JSONArray tags = JSONObject.parseObject(tagBody).getJSONObject("data").getJSONArray("all_theme_detail_list");
            for (int i = 0, len = tags.size(); i < len; i++) {
                JSONObject tag = tags.getJSONObject(i);

                String name = tag.getString("title");
                String id = tag.getString("id");

                if (!Tags.hotSongTag.containsKey(name)) Tags.hotSongTag.put(name, new String[c]);
                Tags.hotSongTag.get(name)[2] = id;
            }
        };

        List<Future<?>> taskList = new LinkedList<>();

        taskList.add(GlobalExecutors.requestExecutor.submit(initHotSongTag));
        taskList.add(GlobalExecutors.requestExecutor.submit(initThemeSongTagKg));

        taskList.forEach(task -> {
            try {
                task.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
    }
}
