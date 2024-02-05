<template>
    <div class="chat-container" v-if="isConnected">
      <h3>채팅</h3>
      <div class="messages">
        <div v-for="chat in chats" :key="chat.id" class="message">
          <img :src="chat.image || '/로고 3.png'" alt="profile" class="thumbnail">
          <span>{{ chat.nickname }}: {{ chat.message }}</span>
        </div>
      </div>
      <input v-model="chatText" @keyup.enter="sendMessage" placeholder="메시지를 입력하세요" />
    </div>
  </template>
  
  <script setup>
  import { ref, reactive, onMounted, onUnmounted } from 'vue';
  import { useCookies } from 'vue3-cookies';
  import { useRoute } from 'vue-router';
  import Stomp from 'webstomp-client';
  import { localAxios } from '@/util/http-commons';
  import { useAccountsStore } from '@/stores/accountsStore'; // accountsStore 가져오기
  
  const { cookies } = useCookies();
  const route = useRoute();
  const roomId = route.params.id; // vue-router를 통해 roomId를 동적으로 받아옴
  const accountStore = useAccountsStore(); // accountsStore 사용
  const chats = ref([]);
  const chatText = ref('');
  const isConnected = ref(false);
  let stompClient = reactive(null);
  
  // 채팅방 내역 불러오기
  const fetchChatHistory = async () => {
    try {
      const response = await localAxios().get(`/chat/history/${roomId}`);
      chats.value = response.data; // 서버에서 채팅 내역을 받아옴
    } catch (error) {
      console.error("Chat history fetch error: ", error);
    }
  };
  
  const connect = () => {
    const serverURL = `${import.meta.env.VITE_VUE_WS_URL}`;
    const accessToken = cookies.get("accessToken");
    const socket = new WebSocket(serverURL);
    stompClient = Stomp.over(socket);
  
    stompClient.connect(
      { Authorization: `Bearer ${accessToken}` },
      async () => {
        isConnected.value = true;
        await fetchChatHistory();
        stompClient.subscribe(`/topic/room.${roomId}`, message => {
          chats.value.push(JSON.parse(message.body));
        });
      },
      error => {
        console.error("Connection error: ", error);
      }
    );
  };
  
  const sendMessage = () => {
    if (chatText.value.trim() !== '') {
      const message = {
        memberId: accountStore.memberInfo.id, // 사용자 ID 설정
        message: chatText.value,
        nickname: accountStore.memberInfo.nickname, // accountsStore에서 닉네임 가져오기
        image: accountStore.memberInfo.image, // accountsStore에서 프로필 이미지 URL 가져오기
        // memberId 설정은 서버에서 처리하거나 필요에 따라 추가
      };
  
      // 메시지 전송 전에 로컬 채팅 목록에 추가
      chats.value.push(message);
  
      stompClient.send(`/pub/chat/${roomId}`, JSON.stringify(message), {
        Authorization: `Bearer ${cookies.get("accessToken")}`
      });
      chatText.value = '';
    }
  };
  
  onMounted(() => {
    connect();
  });
  
  onUnmounted(() => {
    if (stompClient && stompClient.connected) {
      stompClient.disconnect();
    }
  });
  </script>
  
  <style scoped>
  /* 스타일 설정 */
  </style>
  