<template>
  <div id="home" class="home-background">
    <div class="welcome-container">
      <div class="left-content">
        <div class="text-and-logo">
          <h1 class="title">欢迎来到基于SpringBoot的旅游信息交流系统-管理员系统</h1>
        </div>
        <p class="date-time">日期: {{ currentDate }} | 时间: {{ currentTime }}</p>
        <p class="weather">今日天气: {{ weather }}</p>
        <p class="greeting">{{ greetingMessage }} {{ state.name }}</p> <!-- 在此处显示用户名 -->
      </div>
    </div>
    <br>
    <!-- <Dashboard /> -->
  </div>
</template>

<script>
import Dashboard from '../components/Dashboard.vue';
import { useAdminStore } from '@/stores/admin';
import { reactive } from 'vue';
import axios from 'axios'; // 引入 axios

export default {
  name: 'Home',
  components: {
    Dashboard
  },
  setup() {
    const store = useAdminStore();
    const state = reactive({
      name: store.admin.username
    });
    return {
      state
    };
  },
  data() {
    return {
      currentDate: '',
      currentTime: '',
      weather: '正在获取天气...',
      greetingMessage: '欢迎回来',
    };
  },
  created() {
    this.updateDateTime();
    this.setGreetingMessage();
    this.fetchWeather(); // 获取天气数据
    setInterval(this.updateDateTime, 1000);
  },
  methods: {
    updateDateTime() {
      const now = new Date();
      this.currentDate = now.toLocaleDateString();
      this.currentTime = now.toLocaleTimeString();
    },
    setGreetingMessage() {
      const hour = new Date().getHours();
      if (hour < 12) {
        this.greetingMessage = '上午好！';
      } else if (hour < 18) {
        this.greetingMessage = '下午好！';
      } else {
        this.greetingMessage = '晚上好！';
      }
    },
    async fetchWeather() {
      try {
        const response = await axios.get('https://devapi.qweather.com/v7/weather/now', {
          params: {
            location: '101031100', // 北京的城市ID（可以替换为其他城市ID）
            key: 'cd7f7c8e8f404d5b92b15d0a08fe02f2', // 替换为您的和风天气API密钥
          }
        });
        const data = response.data;
        this.weather = `${data.now.text}，${data.now.temp}°C`;
      } catch (error) {
        console.error('无法获取天气数据:', error);
        this.weather = '无法获取天气数据';
      }
    }
  }
};
</script>

<style scoped>
.home-background {
  background: url('../assets/images/icon.jpg');
  background-size: 100% 100%;
  /* 修改为填充满整个页面 */
  height: 78vh;
}

.welcome-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  height: 35vh;
  background-color: rgba(255, 255, 255, 0.7);
  /* 半透明背景 */
  border-radius: 12px;
  box-shadow: 0px 4px 10px rgba(0, 0, 0, 0.1);
}

.left-content {
  flex: 1;
  padding: 20px;
  color: #333;
}

.text-and-logo .title {
  font-size: 36px;
  font-weight: bold;
  color: #2c3e50;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.2);
  margin-bottom: 20px;
}

.date-time,
.weather,
.greeting {
  font-size: 18px;
  color: #555;
  margin: 10px 0;
  line-height: 1.8;
}

.greeting {
  font-size: 24px;
  color: #e67e22;
  font-weight: bold;
  margin-top: 15px;
}

.right-content {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
}

.center-image {
  width: 420px;
  height: auto;
  border-radius: 15px;
  box-shadow: 0px 6px 15px rgba(0, 0, 0, 0.3);
  transition: transform 0.3s;
}

.center-image:hover {
  transform: scale(1.05);
}

@media screen and (max-width: 768px) {
  .welcome-container {
    flex-direction: column;
    height: auto;
  }

  .center-image {
    width: 100%;
    height: auto;
  }
}
</style>
