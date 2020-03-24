package com.lianren.android.improve.bean;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.List;

/**
 * @package: com.lianren.android.improve.bean
 * @user:xhkj
 * @date:2019/12/19
 * @description:
 **/
public class UsersInfoBean implements Serializable {
    public BaseBean base;
    public int type;
    public RequireBean require;
    public AdvanceBean advance;
    public List<String> tag;
    public List<PhotoBean> photo;
    public List<NoteBean> note;

    public static class BaseBean implements Serializable {
        public String bg_image;
        public int id;
        public String uuid;
        public String avatar_url;
        public String nickname;
        public String age;
        public String birthday;
        public String sex;
        public String sex_zh;
        public String height;
        public String weight;
        public String birth_place_id;
        public String birth_place_name;
        public String domicile_id;
        public String domicile_name;
        public String education_code;
        public String education_zh;
        public String revenue_code;
        public String revenue_zh;
        public String school_id;
        public String school_name;
        public String profession_id;
        public String profession_name;
        public String occupation_id;
        public String occupation_name;
        public String industry_id;
        public String industry_name;
        public String marital_status;
        public String marital_status_zh;
        public String is_has_children;
        public String is_has_children_zh;
        public String drink_frequency;
        public String smoke_frequency;
        public String constellation;
        public String constellation_zh;
        public int percent;
        public String status;
        public String about;
        public int auth_status;
        public int contact_status;
        public int pairApply_status;
        public String phone;
    }

    public static class RequireBean implements Serializable {

        public AgeRangeBean age_range;
        public EducationRangeBean education_range;
        public HeightRangeBean height_range;
        public RevenueRangeBean revenue_range;
        public WeightRangeBean weight_range;
        public ChildRangeBean child_range;
        public MaritalRangeBean marital_range;
        public String require_detail;
        public List<DomicileRangeBean> domicile_range;

        public static class AgeRangeBean implements Serializable {
            /**
             * key : -1
             * value : 不限
             */

            public String key;
            public String value;
        }

        public static class EducationRangeBean implements Serializable {
            /**
             * key : -1
             * value : 不限
             */

            public String key;
            public String value;
        }

        public static class HeightRangeBean implements Serializable {
            /**
             * key : -1
             * value : 不限
             */

            public String key;
            public String value;

        }

        public static class RevenueRangeBean implements Serializable {
            /**
             * key : -1
             * value : 不限
             */

            public String key;
            public String value;

        }

        public static class WeightRangeBean implements Serializable {
            /**
             * key : 31+
             * value : 31以上
             */

            public String key;
            public String value;
        }

        public static class ChildRangeBean implements Serializable {
            /**
             * key : -1
             * value : 不限
             */

            public String key;
            public String value;

        }

        public static class MaritalRangeBean implements Serializable {
            /**
             * key : divorce
             * value : 离异
             */

            public String key;
            public String value;
        }

        public static class DomicileRangeBean implements Serializable {
            /**
             * key : -1
             * value : 不限
             */

            public String key;
            public String value;

            public DomicileRangeBean(String key, String value) {
                this.key = key;
                this.value = value;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                DomicileRangeBean person = (DomicileRangeBean) o;
                return TextUtils.equals(value, person.value) && TextUtils.equals(key, person.key);
            }
        }
    }

    public static class AdvanceBean implements Serializable {
        /**
         * vip : {"status":0}
         * ora : {"status":"pass"}
         */

        public VipBean vip;
        public OraBean ora;

        public static class VipBean implements Serializable {
            public int status;
            public String express_time;
            public MealBean meal;

            public static class MealBean implements Serializable{
                public String name;
                public String describe;
            }
        }

        public static class OraBean implements Serializable {

            public String status;

        }
    }

    public static class PhotoBean implements Serializable {

        public int id;
        public String img_uri;
        public int location;

    }

    public static class NoteBean implements Serializable {
        public String id;
        public String tag;
        public String content;
    }
}
