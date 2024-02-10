<template>
    <div class="title" v-if="planDetail">
        <div class="title-name font-title">
            <div :class="[checkName === '0' ? 'active' : 'hidden']" @click="goNameUpdate()">{{ planDetail.name }}</div>
            <form :class="[checkName === '0' ? 'hidden' : 'active']" @submit.prevent="goNameUpdate()">
                <input type="text" v-model="planDetail.name" style="width: 250px; color: gray;">
                <!-- <input class="primary" type="submit" value="확인"> -->
            </form>
        </div>
        <div class="title-date font-content" style="color:silver;">
            <div :class="[checkDate === '0' ? 'active' : 'hidden']" @click="goDateUpdate()">{{ parsedStartDate.year }}년 {{ parsedStartDate.month
            }}월 {{ parsedStartDate.date }}일({{ parsedStartDate.day }}) ~ {{ parsedEndDate.year }}년 {{ parsedEndDate.month }}월 {{ parsedEndDate.date }}일({{ parsedEndDate.day }})</div>
            <form :class="[checkDate === '0' ? 'hidden' : 'active']" @submit.prevent="goDateUpdate()">
                <input type="number" step="1" v-model="parsedStartDate.startYear" id="startYear" style="width: 45px;">년
                <input type="number" step=1 v-model="parsedStartDate.startMonth" min="1" max="12" id="startMonth" style="width: 45px;">월
                <input type="number" step=1 v-model="parsedStartDate.startDate" min="1" max="31" id="startDate" style="width: 45px;">일
                ~
                <input type="number" step="1" v-model="parsedEndDate.endYear" id="endYear" style="width: 45px;">년
                <input type="number" step=1 v-model="parsedEndDate.endMonth" min="1" max="12" id="endMonth" style="width: 45px;">월
                <input type="number" step=1 v-model="parsedEndDate.endDate" min="1" max="31" id="endDate" style="width: 45px;">일
                <input class="primary" type="submit" value="확인">
            </form>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRoute } from "vue-router";
import { planNameUpdateApi, planDateUpdateApi, planGetApi } from "@/api/planApi"
import { usePlanStore } from "@/stores/planStore";

// 여행계획 단일조회 추가
const planStore = usePlanStore()
const checkName = ref('0') // 클래스 체크용
const checkDate = ref('0')  // 클래스 체크용
const planName = ref('텐텐여행1')

const route = useRoute()
const planId = route.params.id;

const planDetail = ref(null);

const goNameUpdate = () => {
    if (checkName.value === '0') {
        checkName.value = '1'
    } else {
        checkName.value = '0'
        const payload = {
            name: planName.value,
        }
        planNameUpdateApi(route.params.id, payload, (response) => {
            if (response.data.dataHeader.successCode === 1) {
                let msg = "계획 이름 변경 중 문제가 발생했습니다.";
                alert(msg);
            } else {
                console.log("계획 이름 변경 성공");
            }
        }, (error) => {
            console.log(error)
        }
        )
    }
}

const goDateUpdate = () => {
    if (checkDate.value === '0') {
        checkDate.value = '1'
    } else {
        checkDate.value = '0'
        const payload = {
            "startDate": `${startYear.value}` + '-' + `${startMonth.value - 1}` - `${startDate.value}`,
            "endDate": `${endYear.value}` + '-' + `${endMonth.value - 1}` - `${endDate.value}`
        }

        planDateUpdateApi(route.params.id, payload, (response) => {
            if (response.data.dataHeader.successCode === 1) {
                let msg = "계획 날짜 변경 중 문제가 발생했습니다.";
                alert(msg);
            } else {
                console.log("계획 날짜 변경 성공");
            }
        }, (error) => {
            console.log(error)
        }
        )
    }
};

// 여행 계획을 가져오는 메서드
const fetchPlanDetail = async () => {
    try {
        const response = await planGetApi(planId);
        if (response.data.dataHeader.successCode === 0) {
            planDetail.value = response.data.dataBody;
            console.log(planDetail.value);
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

// startDate와 endDate의 요일을 계산하는 computed 속성
const getDayOfWeek = (date) => {
  const daysOfWeek = ['일', '월', '화', '수', '목', '금', '토'];
  return daysOfWeek[new Date(date).getDay()];
};

// 시작 및 종료 날짜를 년, 월, 일로 파싱하는 computed 속성
const parsedStartDate = computed(() => {
  if (planDetail.value && planDetail.value.startDate) {
    const date = new Date(planDetail.value.startDate);
    return {
      year: date.getFullYear(),
      month: date.getMonth() + 1,
      date: date.getDate(),
      day: getDayOfWeek(planDetail.value.startDate)
    };
  }
  return { year: '', month: '', date: '', day: '' };
});


const parsedEndDate = computed(() => {
  if (planDetail.value && planDetail.value.endDate) {
    const date = new Date(planDetail.value.endDate);
    return {
      year: date.getFullYear(),
      month: date.getMonth() + 1,
      date: date.getDate(),
      day: getDayOfWeek(planDetail.value.endDate)
    };
  }
  return { year: '', month: '', date: '', day: '' };
});


onMounted(fetchPlanDetail);
</script>

<style scoped>
.title {
    display: flex;
    flex-direction: column;
    gap: 1%;
    padding-left: 10%;
    align-items: start;
    justify-content: end;
}

.title-name {
    font-size: 20px;
}

.title-date {
    font-size: 14px;
}

.active {
    display: block;
}

.hidden {
    display: none;
}
</style>
