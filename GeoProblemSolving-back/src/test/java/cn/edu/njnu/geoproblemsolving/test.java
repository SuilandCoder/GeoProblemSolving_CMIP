package cn.edu.njnu.geoproblemsolving;

import cn.edu.njnu.geoproblemsolving.entity.MethodResource;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: SongJie
 * @Description:
 * @Date: Created in 20:28 2019/9/16
 * @Modified By:
 **/
public class test {

    public static void main(String[] args) {
        Map<String, Object> map = new HashMap<>();
        map.put("age", 24);
        map.put("name", "sdf");
        JSONObject object = new JSONObject(map);
        System.out.println(object);
        Map<String,Object> maptest = (Map<String,Object>)object;
        System.out.println(maptest.toString());

    }

    static class PlayList {
        String name;
        String song[] = {"1", "2", "3"};

        PlayList(String name) {
            this.name = name;
        }
    }

    public static String play(String name, List<PlayList> lists) {
        char[] chars = name.toCharArray();
        List<String[]> outList = new ArrayList<>();
        for (int i = 0; i < chars.length; i++) {
            for (int j = 0; j < lists.size(); j++) {
                PlayList playList = lists.get(j);
                if (playList.name.equals(chars[i] + "")) {
                    String list[] = new String[3];
                    for (int k = 0; k < playList.song.length; k++) {
                        list[k] = playList.name + playList.song[k];
                    }
                    outList.add(list);
                    break;
                }
            }
        }
        play2(outList, 0, 0, "");
        return "res";
    }

    public static void play2(List<String[]> outList, int outIndex, int innerIndex, String res) {

        if (outIndex < outList.size()) {
            for(int i=0;i<3;i++){
                play2(outList, outIndex + 1, i, res + outList.get(outIndex)[i]);
            }
        }else{
            System.out.println(res);
        }
    }



}
