"use strict";
const common_vendor = require("../../common/vendor.js");
const common_assets = require("../../common/assets.js");
const _sfc_main = {
  __name: "index",
  setup(__props) {
    const pics = common_vendor.ref([
      {
        id: "",
        src: "../../static/index/pic1.png",
        text: ""
      },
      {
        id: "",
        src: "../../static/index/pic2.png",
        text: ""
      },
      {
        id: "",
        src: "../../static/index/pic5.png",
        text: ""
      },
      {
        id: "",
        src: "../../static/index/pic4.png",
        text: ""
      },
      {
        id: "",
        src: "../../static/index/pic3.png",
        text: ""
      }
    ]);
    const navigateToPage = (id, title) => {
      console.log("==== 进来了 :", id);
      common_vendor.index.navigateTo({
        url: "/pages/firstCourse/firstCourse?id=" + id + "&title=" + title
      });
    };
    common_vendor.onMounted(async () => {
      common_vendor.index.request({
        url: "http://localhost:8080/category/categoryList",
        // 确保URL正确
        method: "GET",
        success: (res) => {
          if (res.statusCode === 200 && res.data.code === 1001) {
            res.data.data.forEach((item, i) => {
              pics.value[i].id = item.id;
              pics.value[i].text = item.categoryName;
            });
            console.log("==== pics :", pics.value);
          } else {
            console.error("没找到当前页面:", res.data.message);
          }
        },
        fail: (err) => {
          console.error("错误的请求:", err);
        }
      });
    });
    const currentIndex = common_vendor.ref(0);
    const tagRefs = common_vendor.ref([]);
    const scrollViewRef = common_vendor.ref(null);
    common_vendor.ref(null);
    const studentId = common_vendor.ref(common_vendor.index.getStorageSync("studentId"));
    const error = common_vendor.ref(null);
    const recommendItems = common_vendor.ref([{
      "courseName": "桂林山水甲天下",
      "courseDescription": "桂林山水值得一看",
      "coverPic": "/static/index/桂林1.jpg"
    }, {
      "courseName": "北京故宫",
      "courseDescription": "北京故宫值得一看",
      "coverPic": "/static/index/故宫.jpg"
    }, {
      "courseName": "杭州西湖",
      "courseDescription": "来尝尝很火的西湖醋鱼吧",
      "coverPic": "/static/index/杭州.jpg"
    }]);
    const underlinePosition = common_vendor.ref(0);
    const underlineWidth = common_vendor.ref(0);
    const updateUnderlinePosition = async () => {
      var _a, _b, _c;
      await common_vendor.nextTick$1();
      if (!tagRefs.value[currentIndex.value])
        return;
      const rect = (_a = tagRefs.value[currentIndex.value].$el) == null ? void 0 : _a.getBoundingClientRect();
      if (!rect)
        return;
      const parentRect = (_b = scrollViewRef.value) == null ? void 0 : _b.$el.getBoundingClientRect();
      const scrollLeft = ((_c = scrollViewRef.value) == null ? void 0 : _c.scrollLeft) || 0;
      underlinePosition.value = rect.left - ((parentRect == null ? void 0 : parentRect.left) || 0) + scrollLeft;
      underlineWidth.value = rect.width;
    };
    common_vendor.onMounted(async () => {
      if (!studentId.value) {
        error.value = "用户ID未找到";
        return;
      }
      getUserLikes();
    });
    common_vendor.watch(currentIndex, updateUnderlinePosition);
    const swiperCurrent = common_vendor.ref(0);
    const switchContent = () => {
      const nextIndex = (swiperCurrent.value + 1) % recommendItems.value.length;
      swiperCurrent.value = nextIndex;
    };
    const onSwiperChange = (e) => {
      swiperCurrent.value = e.detail.current;
    };
    async function getUserLikes() {
      try {
        const response = await common_vendor.index.request({
          url: "http://localhost:8080/course/guest",
          method: "GET",
          data: {
            studentId: studentId.value
          },
          header: {
            "content-type": "application/x-www-form-urlencoded"
          }
        });
        if (response.data.code === 1001) {
          recommendItems.value = response.data.data;
          console.log("==== recommendItems.value :", response.data.data);
          console.log("==== recommendItems.value :", recommendItems.value);
        } else {
          throw new Error("无法获取用户喜好");
        }
      } catch (err) {
        console.error("Error fetching user likes:", err);
        error.value = "无法获取用户喜好";
        return [];
      }
    }
    const otherItems = common_vendor.ref([
      {
        image: "../../static/index/北海银滩.jpg",
        title: "北海银滩 ",
        description: "广西夏天很不错的去处",
        tags: ["海边推荐"]
      },
      {
        image: "../../static/index/颐和园.jpg",
        title: "北京颐和园 ",
        description: "来北京感受历史文化",
        tags: ["历史建筑"]
      },
      {
        image: "../../static/index/少林寺.jpg",
        title: "少林寺",
        description: "看看影视剧中的真实样子",
        tags: ["历史建筑", "影视文化"]
      },
      {
        image: "../../static/index/龙虎山.jpg",
        title: "龙虎山",
        description: "惊艳眼球的自然景观",
        tags: ["自然景观", "大自然"]
      }
      // 添加更多静态其他项目...
    ]);
    return (_ctx, _cache) => {
      return {
        a: common_assets._imports_0,
        b: common_assets._imports_1,
        c: common_assets._imports_2,
        d: common_assets._imports_3,
        e: common_assets._imports_4,
        f: common_assets._imports_5,
        g: common_assets._imports_6,
        h: common_vendor.f(pics.value, (pic, index, i0) => {
          return {
            a: pic.src,
            b: common_vendor.t(pic.text),
            c: index,
            d: common_vendor.o(($event) => navigateToPage(pic.id, pic.text), index)
          };
        }),
        i: common_vendor.f(otherItems.value, (item, index, i0) => {
          return {
            a: item.image,
            b: common_vendor.t(item.title),
            c: common_vendor.t(item.description),
            d: common_vendor.f(item.tags, (tag, tagIndex, i1) => {
              return {
                a: common_vendor.t(tag),
                b: tagIndex
              };
            }),
            e: index
          };
        }),
        j: common_assets._imports_7,
        k: common_vendor.o(switchContent),
        l: common_vendor.f(recommendItems.value, (item, index, i0) => {
          return {
            a: item.coverPic,
            b: common_vendor.t(item.courseName),
            c: common_vendor.t(item.courseDescription),
            d: index
          };
        }),
        m: swiperCurrent.value,
        n: common_vendor.o(onSwiperChange)
      };
    };
  }
};
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-1cf27b2a"]]);
wx.createPage(MiniProgramPage);
