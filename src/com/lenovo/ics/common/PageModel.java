package com.lenovo.ics.common;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONObject;

public class PageModel {

    private static final ThreadLocal<PageModel> sPageModelKey = new ThreadLocal<PageModel>();

    private int curPage;
    private int pageSize;
    private int pageCount;

    private int index;

    /**
     * 返回当前页码
     * 
     * @return
     */
    public int getCurPage() {
        return curPage;
    }

    /**
     * 返回页面记录条数
     * 
     * @return
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * 返回总页数
     * 
     * @return
     */
    public int getPageCount() {
        return pageCount;
    }

    /**
     * 返回数据库分页查询的起始位置
     * 
     * @return
     */
    public int getIndex() {
        return index;
    }

    /**
     * 使用传入的总记录条数来计算数据库分页查询的起始位置
     * 
     * @param count
     *            总记录条数
     */
    public void reInit(int count/* 总记录条数 */) {
        pageCount = (int) Math.ceil(((float) count) / pageSize);

        if (curPage > pageCount) {
            curPage = pageCount;
        }
        if (curPage < 1) {
            curPage = 1;
        }

        index = (curPage - 1) * pageSize;
    }

    public void toJson(JSONObject json) {
        json.put("curPage", getCurPage());
        json.put("pageSize", getPageSize());
        json.put("pageCount", getPageCount());
    }

    /**
     * 将PageModel实例保存在ThreadLocal中，以便Service和DAO层进行数据获取
     */
    public void set() {
        sPageModelKey.set(this);
    }

    /**
     * 从ThreadLocal中获取PageModel对象，如果不存在，则返回null
     */
    public static final PageModel get() {
        return sPageModelKey.get();
    }

    /**
     * 将分页相关的数据写入json对象返回给终端
     * 
     * @param json
     */
    public static final void writeToJson(JSONObject json) {
        PageModel model = get();
        if (model != null) {
            model.toJson(json);
        }
    }

    /**
     * 从终端传过来的json数据中获取分页相关的数据，并保存到ThreadLocal
     * 
     * @param json
     * @return
     */
    public static void readFromJson(JSONObject json) {
        String isPage = null;
        if (json.containsKey("isPage")) {
            isPage = json.getString("isPage");
        }
        if (!StringUtils.isBlank(isPage) && isPage.matches("1")) {
            PageModel model = new PageModel();
            if (json.containsKey("curPage")) {
                try {
                    model.curPage = json.getIntValue("curPage");
                } catch (Exception e) {
                    model.curPage = 1;
                }
            } else {
                model.curPage = 1;
            }
            if (json.containsKey("pageSize")) {
                try {
                    model.pageSize = json.getIntValue("pageSize");
                } catch (Exception e) {
                    model.pageSize = Resource.pageSize();
                }
            } else {
                model.pageSize = Resource.pageSize();
            }

            model.set();
        }
    }

}