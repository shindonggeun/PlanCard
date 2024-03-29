<template>
  <v-card id="popUp">
    <v-list id="profileInfo">
      <v-list-item :prepend-avatar="accountsStore.memberInfo?.image ? accountsStore.memberInfo.image : '/로고 3.png'"
      style="padding-top: 0; padding-bottom: 0;">
        <template #title>
          <span style="font-size: 18px; color: #3498DB;">
            {{ accountsStore.memberInfo?.name }}
          </span>
        </template>
        <template #subtitle>
          <span style="font-size: 11px;">{{ accountsStore.memberInfo?.email }}</span>
        </template>
      </v-list-item>
    </v-list>
    <div @click="goMyPage()" style="text-align: center;">
      <v-btn id="myPageBtn">내 정보</v-btn>
      <v-btn id="logOutBtn" @click="logout()">로그아웃</v-btn>
    </div>
    <v-divider></v-divider>
    <div id="friendsList" ref="friendsList" @scroll="handleScroll">
      <div style="display: flex;">
        <p style="font-size: 20px; padding-left: 10px; margin-bottom: 0px; margin-top: 3px; color: rgba(0, 0, 0, 0.5);">
          친구 목록
        </p>
        <v-btn id="requestBtn" @click="goFriendRequest()">친구 요청</v-btn>
      </div>

      <div v-if="friends.length > 0" >
        <div v-for="friend in friends" :key="friend.friendId" id="friendInfo">
          <div>
            <img :src="friend.image || '/로고 3.png'" alt="프로필 이미지" id="friendProfileImg">
          </div>
          <div>
            <div style="display: flex;">
              <p id="friendName">{{ friend.name }} </p>
              <p :class="friend.status ? 'onLine' : 'offLine'">&nbsp;●</p>
            </div>
            <p id="friendEmail">{{ friend.email }}</p>
          </div>
        </div>
      </div>

      <div v-else>
        <p style="text-align: center; color: rgba(0, 0, 0, 0.5); padding: 10px;">친구가 없습니다.</p>
      </div>

    </div>
    <v-divider></v-divider>
    <v-card-actions>
      <v-btn variant="text" @click="$emit('closeProfile')" id="closeBtn">
        <p>닫기</p>
      </v-btn>
    </v-card-actions>

    <!-- 친구 요청 입력 modal -->
    <div>
      <div v-if="friendRequestActive" id="overlay" @close-friend-request="closeFriendRequest" @click="closeFriendRequest"></div>
      <FriendApply v-if="friendRequestActive" @close-friend-request="closeFriendRequest" style="z-index: 998;" />
    </div>

  </v-card>
</template>



<script setup>
import { ref, onUnmounted, onMounted, getCurrentInstance, nextTick, watch } from 'vue';
import { useRouter } from 'vue-router';
import { useAccountsStore } from '@/stores/accountsStore';
import { memberLogoutApi } from "@/api/memberApi";
import FriendApply from '@/layout/items/FriendApply.vue';
import { friendListGetApi } from '@/api/friendApi';
const accountsStore = useAccountsStore()
const router = useRouter()
const { emit } = getCurrentInstance();

const friends = ref([]); // 현재 페이지에 표시된 친구들의 목록
const page = ref(0); // 현재 페이지 번호
const size = ref(10); // 페이지당 아이템 수
const isLastPage = ref(false); // 마지막 페이지 여부

// props로 상태를 받습니다.
const props = defineProps({
  profileActive: Boolean
});

// props.profileActive 변화를 감지합니다.
watch(() => props.profileActive, (newVal) => {
  if (newVal) {
    console.log(newVal);
    fetchFriendList(); // 프로필 팝업이 활성화될 때마다 친구 목록을 새로고침합니다.
  }
});


// MyPage로 이동
const goMyPage = function () {
  router.push({ name: "member-mypage" });
  emit('closeProfile');
}

// 로그아웃 요청 메서드
const logout = async () => {
  try {
    await memberLogoutApi(
      (response) => {
        if (response.data.dataHeader.successCode === 0) {
          accountsStore.setLogout();
          router.push({ name: 'main' });  // 로그아웃 성공 후 메인 페이지로 이동
        }
        // 서버에서 실패 응답 받았을 경우 (즉, redis에 저장된 refresh 토큰 만료돼서 삭제 된 경우인데 이럴 일 사실상 거의 없음)
        else {
          alert(response.data.dataHeader.resultMessage);
          accountsStore.setLogout();
          router.push({ name: 'main' });  // 로그아웃 성공 후 메인 페이지로 이동
        }
      }
    )
  } catch (error) {
    if (error.response) {
      console.error(error);
      const errorResponse = error.response.data;
      alert(errorResponse.dataHeader.resultMessage);
      accountsStore.setLogout();
      router.push({ name: 'main' });  // 로그아웃 성공 후 메인 페이지로 이동
    } else if (error.message === 'Network Error' || error.code === 'ERR_NETWORK') {
      // 네트워크 에러 처리
      alert("서버에 연결할 수 없습니다. 네트워크 연결을 확인해주세요.");
    }
  }
}

// 친구 목록을 불러오는 메서드
const fetchFriendList = async () => {
  if (isLastPage.value) return; // 마지막 페이지이면 더 이상 불러오지 않음
  try {
    const response = await friendListGetApi(page.value, size.value);
    if (response.data.dataHeader.successCode === 0) {;
      friends.value.push(...response.data.dataBody.contents); // 받아온 친구 목록을 추가
      await nextTick(); // DOM 갱신을 기다립니다.
      isLastPage.value = !response.data.dataBody.hasNext; // 다음 페이지 존재 유무 업데이트
      page.value++; // 다음 페이지로 이동

    } else {
      alert(response.data.dataHeader.resultMessage);
    }
  } catch (error) {
    if (error.response) {
      console.error(error);
      const errorResponse = error.response.data;
      alert(errorResponse.dataHeader.resultMessage);
    } else if (error.message === 'Network Error' || error.code === 'ERR_NETWORK') {
      // 네트워크 에러 처리
      alert("서버에 연결할 수 없습니다. 네트워크 연결을 확인해주세요.");
    }
  }
};

// 스크롤 이벤트 핸들러
const handleScroll = () => {
  const { scrollTop, scrollHeight, clientHeight } = document.documentElement;
  if (scrollTop + clientHeight >= scrollHeight - 5) { // 스크롤이 거의 끝에 도달하면
    fetchFriendList(); // 다음 페이지의 친구 목록을 불러옴
  }
};

const friendRequestActive = ref(false);  // 친구요청 팝업 on/off 변수
// 친구요청 팝업 on/off
const goFriendRequest = () => {
  friendRequestActive.value = !friendRequestActive.value;
};
const closeFriendRequest = () => {
  friendRequestActive.value = false;
}


onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll);
});

</script>



<style scoped>
#popUp {
  width: 330px;
  border: 1px solid rgba(52, 152, 219, 0.5);
  /* border-radius: 5%; */
  padding: 7px;
  top: 60px;
}
#profileInfo {
  width: 100%;
  border-radius: 15px;
  border: 1px solid rgba(52, 152, 219, 0.5);
}

#myPageBtn {
  text-align: center;
  color: #FFFFFF;
  background-color: #3498DB;
  position: relative;
  top: 5px;
  margin-right: 1%;
  width: 49%;
  height: 30px;
  padding: 0.8%;
  border-radius: 5cm;
  border: 1px solid rgba(52, 152, 219, 0.5);
  font-size: 15px;
}
#myPageBtn:hover {
  transform: scale(1.05);
    border-color: #3498db;
}
#logOutBtn {
  text-align: center;
  color: rgba(0, 0, 0, 0.5);
  background-color: #FFFFFF;
  position: relative;
  top: 5px;
  margin-left: 1%;
  width: 49%;
  height: 30px;
  padding: 0.8%;
  border-radius: 5cm;
  /* border: 1px solid rgba(52, 152, 219, 0.5); */
  font-size: 15px;
}
#logOutBtn:hover {
  transform: scale(1.05);
  border-color: #3498db;
}

#friendsList {
  background-color: #FFFFFF;
  border-radius: 15px;
  border: 1px solid rgba(52, 152, 219, 0.5);
  max-height: 200px;
  overflow-y: auto;
  margin-bottom: -50px;
  margin-top: -17px;
}

#requestBtn {
  text-align: center;
  color: #FFFFFF;
  background-color: #3498DB;
  position: relative;
  /* font-weight: bold; */
  border-radius: 5cm;
  border: 1px solid rgba(0, 0, 0, 0.1);

  width: 22%;
  height: 15%;
  font-size: 11px;

  margin-left: 120px;
  margin-top: 5px;
  padding: 3px 10px 3px 10px;
}
#requestBtn:hover {
  transform: scale(1.05);
  border-color: #3498db;
}

#friendInfo {
  display: flex;
  align-items: center;
  /* margin-bottom: -10px; */
  margin-left: 10px;
}
#friendProfileImg {
  border-radius: 50%;
  width: 40px;
  height: 40px;
  margin-right: 5px;
}
#friendName {
  color: #3498DB;
  font-size: 18px;
  /* font-weight: bold; */
  margin-bottom: 0;
}
#friendEmail {
  color: rgba(0, 0, 0, 0.5);
  font-size: 11px;
  margin-top: -6px;
  margin-bottom: 5px;
  margin-left: 1px;
}
.onLine {
  color: #2ECC71;
  font-size: 11px;
}
.offLine {
  color: #808080;
  font-size: 11px;
  padding-top: 4px;
  padding-left: 5px;
}

#closeBtn {
  text-align: center;
  color: #FFFFFF;
  background-color: #3498DB;
  position: relative;
  width: 100%;
  height: 25px;
  line-height: 25px;
  /* font-weight: bold; */
  border-radius: 5cm;
  border: 1px solid rgba(0, 0, 0, 0.1);
  top: 15px;
}
#closeBtn:hover {
  transform: scale(1.05);
  border-color: #3498db;
}



/* 친구요청창 CSS */
#overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  /* 어두운 배경 */
  z-index: 997;
  /* 모달보다 한 단계 낮은 z-index */
}
#friendRequestBox {
  position: fixed;
  top: 30%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 9999;
}

#friendRequestTitle {
  color: #3498db;
  /* font-weight: bold; */
  margin: 0;
}

#friendRequestForm {
  border: none;
  margin-bottom: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 10px;
  padding-bottom: 0;
}
#friendRequestInput {
  background-color: rgba(245, 245, 245, 0.1);
  width: 300px;
  border: 1px solid rgba(52, 152, 219, 0.5);
  margin: 5px;
  text-align: center;
  /* 입력 내용을 가운데 정렬합니다. */
  padding: 8px;
  font-size: large;
}
#friendRequestSubmit {
  width: 100px;
  height: 30px;
  text-align: center;
  color: #FFFFFF;
  background-color: #3498DB;
  position: relative;
  /* font-weight: bold; */
  border-radius: 5cm;
  border: 1px solid rgba(0, 0, 0, 0.1);
  padding: 10px;
  margin-top: 5px;
  line-height: 0px;
}
#friendRequestSubmit:hover {
  transform: scale(1.05);
  border-color: #3498db;
}
#friendsRequestcloseBtn {
  text-align: center;
  color: #FFFFFF;
  background-color: #3498DB;
  position: relative;
  width: 100%;
  height: 25px;
  line-height: 25px;
  /* font-weight: bold; */
  border-radius: 5cm;
  border: 1px solid rgba(0, 0, 0, 0.1);
  top: 15px;
}
</style>