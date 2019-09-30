package cn.edu.njnu.geoproblemsolving;

import cn.edu.njnu.geoproblemsolving.entity.MethodResource;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: SongJie
 * @Description:
 * @Date: Created in 20:28 2019/9/16
 * @Modified By:
 **/
public class test {
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

    public static void main(String[] args) {
//        List<PlayList> lists = new ArrayList<>();
//        PlayList aList = new PlayList("A");
//        PlayList bList = new PlayList("B");
//        PlayList cList = new PlayList("C");
//        PlayList dList = new PlayList("D");
//        PlayList eList = new PlayList("E");
//        lists.add(aList);
//        lists.add(bList);
//        lists.add(cList);
//        lists.add(dList);
//        lists.add(eList);
//        play("ACBD", lists);
//        JSONObject object = new JSONObject();
//        object.put("date", "2019-09-16T07:12:14.000Z");
//
//        Date date = object.getDate("date");
//        System.out.println(date);

//        MethodResource methodResource = new MethodResource();
//        System.out.println(methodResource.toString());
    }

}
