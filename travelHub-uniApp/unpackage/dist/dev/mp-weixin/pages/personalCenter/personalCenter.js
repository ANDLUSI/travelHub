"use strict";
const common_vendor = require("../../common/vendor.js");
const common_assets = require("../../common/assets.js");
const _sfc_main = {
  __name: "personalCenter",
  setup(__props) {
    const user = common_vendor.ref({
      data: {
        title: "",
        imagePath: ""
        // 默认头像路径为空
      }
    });
    const loginFlag = common_vendor.ref(false);
    const findById = async () => {
      console.log("findById进入了");
      if (user.value != null) {
        console.log("findById的if进入了");
        user.value = common_vendor.index.getStorageSync("user");
        console.log("findById的user.value" + user.value);
        loginFlag.value = true;
        return;
      }
      const res = await common_vendor.index.request({
        url: "http://localhost:8080/user/findById",
        method: "GET",
        data: {
          studentId: studentId.value
        }
      });
      if (res.data.message === "操作成功") {
        student.value.data = res.data.data;
        common_vendor.index.showToast({
          title: "登录成功",
          icon: "none"
        });
        loginFlag.value = true;
      } else {
        common_vendor.index.showToast({
          title: "当前处于未登录状态",
          icon: "none"
        });
      }
    };
    common_vendor.onMounted(() => {
      findById();
    });
    const outLogin = async () => {
      common_vendor.index.showToast({
        title: "退出登录",
        icon: "none"
      });
      common_vendor.index.setStorageSync("studentId", "");
      common_vendor.index.setStorageSync("teacherId", "");
      common_vendor.index.setStorageSync("secretKey", "");
      loginFlag.value = false;
      student.data = "{}";
    };
    const toModify = () => {
      common_vendor.index.navigateTo({
        url: "/pages/personalCenter/modifyStudnet"
      });
    };
    const toLogin = () => {
      common_vendor.index.navigateTo({
        url: "/pages/loginAndReg/index"
      });
    };
    return (_ctx, _cache) => {
      return {
        a: common_assets._imports_0$2,
        b: user.value.data.imagePath || "https://learnhub.oss-cn-beijing.aliyuncs.com/images/2024/12/05/1733378526228.png",
        c: common_vendor.t(user.value.data.title),
        d: common_assets._imports_1$2,
        e: common_vendor.o(toModify),
        f: loginFlag.value,
        g: common_vendor.o(toLogin),
        h: !loginFlag.value,
        i: common_assets._imports_2$2,
        j: common_assets._imports_3$2,
        k: common_vendor.o(outLogin)
      };
    };
  }
};
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-7b035f71"]]);
wx.createPage(MiniProgramPage);
