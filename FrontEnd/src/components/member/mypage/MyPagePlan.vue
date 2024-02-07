<template>
  <div class="card p-fluid">

    <div id="div1">
      <h1>나의 여행 계획들</h1>
      <v-btn class="startBtn" id="addBtn" @click="showCreateMeeting">
        <p style="margin-bottom: 20%;">+</p>
      </v-btn>
    </div>

    <hr id="separator">

    <div v-for="plan in plans" :key="plan.index" class="card f-fluid" id="plan">
      <div id="planName">
        <p>{{ plan.name }}</p>
      </div>
      <div id="div2">
        <p class="card f-fluid" id="planPeople">인원 : {{ plan.people }}명</p>
        <p class="card f-fluid" id="planDate">{{ plan.startDate }} ~ {{ plan.EndDate }}</p>
        <v-btn class="startBtn">
          <router-link :to="{ name: 'meeting-detail', params: { id: plan.id } }" class="router-link-active">
            Start
          </router-link>
        </v-btn>
      </div>
    </div>

    <div id="overlay" v-if="showCreateMeetingModal"></div>
    <div id="createMeetingBox">
      <MeetingCreate v-if="showCreateMeetingModal" @close-meeting-create="closeMeetingCreate"></MeetingCreate>
    </div>

  </div>
</template>



<script setup>
import { onMounted, ref } from 'vue';
import router from "@/router";
import MeetingCreate from "@/components/meeting/MeetingCreate.vue";
import { planListGetApi } from "@/api/planApi"; // API 함수를 import


const showCreateMeetingModal = ref(false);
const plans = ref([]); // 초기 plans를 빈 배열로 설정

const closeMeetingCreate = () => {
  showCreateMeetingModal.value = false;
}

const showCreateMeeting = () => {
  showCreateMeetingModal.value = !showCreateMeetingModal.value;
}

// 여행 계획 목록을 불러오는 함수
const fetchPlans = async () => {
  console.log("이벤트 발생된거 확인 해보자!!!");
  try {
    const response = await planListGetApi();
    if(response.data.dataHeader.successCode === 0) {
      plans.value = response.data.dataBody.map(plan => ({
        ...plan,
        people: plan.planMemberCount, // API 응답과 컴포넌트 내 변수명 매핑
      startDate: plan.startDate.split('-').join('.'),
      EndDate: plan.endDate.split('-').join('.'),
      }))
    } else {
      alert(response.data.dataHeader.resultMessage);
    }
    

  } catch (error) {
    console.error(error);
    alert("나의 여행 계획 리스트 불러오기 중 오류가 발생했습니다.");
  }
}

onMounted(fetchPlans); // 컴포넌트가 마운트될 때 여행 계획 목록을 불러옴
</script>



<style scoped>
#div1 {
  display: flex;
  /* 하위 요소들을 가로 정렬. */
  flex-wrap: nowrap;
  /* 하위 요소들을 한 줄에 모두 표시. */
  align-items: center;
  /* 하위 요소들을 수직 가운데 정렬. */
  justify-content: space-between;
  /* 좌우 정렬 */
}

#addBtn {
  background-color: #3498DB;
  color: white;
  font-size: 40px;
  font-weight: bold;
  padding: 0;
  margin: 0;

  display: flex;
  justify-content: center;
  align-items: center;
}

h1 {
  font-weight: bold;
}

#separator {
  border: none;
  border-top: 4px solid #3498DB;
  border-radius: 10px;
}

#plan {
  border: 1px solid rgba(0, 0, 0, 0.1);
  background-color: #EAF9FF;
  height: 70px;
  display: flex;
  /* 하위 요소들을 가로 정렬. */
  flex-wrap: nowrap;
  /* 하위 요소들을 한 줄에 모두 표시. */
  align-items: center;
  /* 하위 요소들을 수직 가운데 정렬. */
}

#planName {
  color: #3498DB;
  font-weight: bold;
  font-size: xx-large;
  margin-top: 0;
  margin-bottom: 0;
  width: 70%;

  /* 작아지면 텍스트 가리기 */
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

#div2 {
  display: flex;
  flex-wrap: nowrap;
  width: 50%;
  justify-content: flex-end;
}

#planPeople {
  border: 1px solid rgba(0, 0, 0, 0.1);
  color: black;
  font-weight: bold;
  font-size: larger;
  margin-right: 2%;
  margin-left: 2%;
  margin-top: 0;
  margin-bottom: 0;
  padding: 2%;
  padding-top: 1%;
  padding-bottom: 1%;
}

#planDate {
  border: 1px solid rgba(0, 0, 0, 0.1);
  color: black;
  font-weight: bold;
  font-size: larger;
  margin-right: 2%;
  margin-left: 2%;
  margin-top: 0;
  margin-bottom: 0;
  padding: 2%;
  padding-top: 1%;
  padding-bottom: 1%;
}

.startBtn {
  border: 1px solid rgba(0, 0, 0, 0.1);
  background-color: #3498DB;
  color: #FFFFFF;
  font-weight: bold;
  margin-right: 2%;
  margin-left: 2%;
}

.router-link-active {
  /* router-link의 글자 색이 변하지 않게 하는 css */
  background-color: transparent !important;
  /* 배경색을 투명으로 설정 */
  color: inherit !important;
  /* 글자색을 상속 받음 */
}





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

#createMeetingBox {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 998;
  max-height: 70vh;
  overflow-y: auto;
}</style>