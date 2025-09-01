"use strict";
const common_vendor = require("../../common/vendor.js");
const common_assets = require("../../common/assets.js");
if (!Array) {
  const _easycom_uni_easyinput2 = common_vendor.resolveComponent("uni-easyinput");
  const _easycom_uni_forms_item2 = common_vendor.resolveComponent("uni-forms-item");
  const _easycom_uni_forms2 = common_vendor.resolveComponent("uni-forms");
  (_easycom_uni_easyinput2 + _easycom_uni_forms_item2 + _easycom_uni_forms2)();
}
const _easycom_uni_easyinput = () => "../../uni_modules/uni-easyinput/components/uni-easyinput/uni-easyinput.js";
const _easycom_uni_forms_item = () => "../../uni_modules/uni-forms/components/uni-forms-item/uni-forms-item.js";
const _easycom_uni_forms = () => "../../uni_modules/uni-forms/components/uni-forms/uni-forms.js";
if (!Math) {
  (_easycom_uni_easyinput + _easycom_uni_forms_item + _easycom_uni_forms)();
}
const _sfc_main = {
  __name: "index",
  setup(__props) {
    const formRef = common_vendor.ref();
    const loginForm = common_vendor.ref({
      name: "",
      password: "",
      identity: ""
    });
    const rules = {
      name: {
        rules: [
          { required: true, errorMessage: "请输入用户名" }
        ]
      },
      password: {
        rules: [
          { required: true, errorMessage: "请输入密码" },
          { minLength: 6, maxLength: 15, errorMessage: "密码长度在{minLength}--{maxLength}之间" }
        ]
      },
      identity: {
        rules: [
          { required: true, errorMessage: "请选择您的身份" }
        ]
      }
    };
    const urlm = common_vendor.ref("http://localhost:8080/user/login1");
    const login = () => {
      formRef.value.validate().then(async (r) => {
        console.log("验证成功");
        console.log(r);
        urlm.value = "http://localhost:5173/api/login";
        try {
          const formData = {
            username: loginForm.value.name,
            password: loginForm.value.password,
            identity: loginForm.value.identity
            // 如果服务器端需要这个信息的话
          };
          const res = await common_vendor.index.request({
            url: urlm.value,
            method: "POST",
            data: formData,
            header: {
              "content-type": "application/x-www-form-urlencoded"
            }
          });
          console.log("===========>>>>", res);
          if (res.data.state == 0) {
            common_vendor.index.showToast({ title: "登录成功！" });
            console.log("===========>>>>", res.data);
            common_vendor.index.setStorageSync("token", res.data.data.message);
            common_vendor.index.setStorageSync("user", res.data.data);
            common_vendor.index.reLaunch({ url: "/pages/personalCenter/personalCenter" });
          } else {
            common_vendor.index.showToast({ title: "登录失败！", icon: "none" });
          }
        } catch (error) {
          console.error(error);
          common_vendor.index.showToast({ title: "登录失败！", icon: "none" });
        }
      }).catch((err) => {
        console.log("验证失败");
        console.log(err);
      });
    };
    return (_ctx, _cache) => {
      return {
        a: common_assets._imports_0,
        b: common_vendor.o(($event) => loginForm.value.name = $event),
        c: common_vendor.p({
          placeholder: "请输入用户名",
          modelValue: loginForm.value.name
        }),
        d: common_vendor.p({
          required: true,
          label: "用户名",
          name: "name"
        }),
        e: common_vendor.o(($event) => loginForm.value.password = $event),
        f: common_vendor.p({
          type: "password",
          placeholder: "请输入密码",
          modelValue: loginForm.value.password
        }),
        g: common_vendor.p({
          required: true,
          label: "密码",
          name: "password"
        }),
        h: common_vendor.sr(formRef, "d3b80979-0", {
          "k": "formRef"
        }),
        i: common_vendor.o(($event) => loginForm.value = $event),
        j: common_vendor.p({
          rules,
          ["label-width"]: 100,
          modelValue: loginForm.value
        }),
        k: common_vendor.o(login)
      };
    };
  }
};
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-d3b80979"]]);
wx.createPage(MiniProgramPage);
