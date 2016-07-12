package dong.lan.mapeye.utils;

import android.content.Context;
import android.media.SoundPool;
import android.os.Build;

import java.util.HashMap;

/**
 * 项目：  MapEye
 * 作者：  梁桂栋
 * 日期：  7/3/2016  17:35.
 * Email: 760625325@qq.com
 */
public class SoundHelper {

    HashMap<Integer, Integer> soundMap = new HashMap<>();
    SoundPool soundPool;


    public SoundHelper init(int maxStreams) {
        if (Build.VERSION.SDK_INT >= 21) {
            SoundPool.Builder builder = new SoundPool.Builder();
            builder.setMaxStreams(maxStreams);
            soundPool = builder.build();
        } else {
            soundPool = new SoundPool(maxStreams, 0, 8);
        }
        return this;
    }

    public void play(Context context, int rawId, int replay) {
        if (replay != 0 && replay != -1)
            replay = 0;
        if (!soundMap.containsKey(rawId)) {
            soundMap.put(rawId, soundPool.load(context, rawId, 1));
        }
        soundPool.play(soundMap.get(rawId), 1, 1, 0, replay, 1);
    }


    public void loadAll(Context context,int[] rawIds) {
        for (int rawId : rawIds) {
            soundMap.put(rawId, soundPool.load(context, rawId, 1));
        }
    }

    public void release() {
        soundMap = null;
        soundPool.release();
    }
}
