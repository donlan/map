package dong.lan.mapeye.contracts;

import dong.lan.mapeye.model.Record;

/**
 * Created by 梁桂栋 on 16-11-1 ： 下午7:28.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: 用户记录的围栏与路径的契约类
 */

public final class RecordContract {
    private RecordContract(){}

    /**
     * 用户记录的围栏与路径的View层接口
     */
    public interface RecordView{

        //加载数据
        void loadData();
        //刷新指定条目
        void refresh(int index);
        //删除指定条目
        void delete(int index);
        //点击制定条目后跳转
        void jump(int index);

        void insertRecord(int position);

        //设置适配器
        void setAdapter();

        //加载空列表的替代view
        void setUpEmptyView();
    }

    /**
     * 用户记录的围栏与路径的Presenter层接口
     */
    public interface Presenter {
        //装载数据
        void load();
        //返回记录的大小
        int getRecordSize();

        /**
         * @param position 列表中的位置
         * @return 返回列表指定位置的Record
         */
        Record getRecord(int position);

        /**
         * 处理新添加的记录并刷新列表
         * @param record
         */
        void addAndRefresh(Record record);

        /**
         * 删除一个记录
         * @param index 待删除记录的位置
         */
        void removeRecord(int index);
    }
}
