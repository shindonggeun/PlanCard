<script setup>
import ItemTitle from '@/components/meeting/items/ItemTitle.vue'
import draggable from "@/vuedraggable";
import KaKaoMap from '@/components/common/map/KaKaoMap.vue'
import { ref, computed, watch, onMounted, onBeforeMount, getCurrentInstance } from "vue";
import { useRoute, useRouter } from 'vue-router';
import { WebsocketProvider } from 'y-websocket';
// import * as Y from 'yjs';
import _ from 'lodash'
import { debounce } from "lodash";
import { cardListGetApi, cardCreateApi, placeSearchApi } from '@/api/cardApi';
import { planDetailCreateApi, planDetailListGetApi } from '@/api/planApi';
import { doc, yCardList, yPlanList } from '@/api/yjs';
import { usePlanStore } from "@/stores/planStore";
const router = useRouter()
const route = useRoute();
const planStore = usePlanStore()

const planId = route.params.id; // URL에서 planId를 추출합니다.
const cardListRaw = ref([]); // 카드 목록을 담을 반응형 변수를 선언합니다.
const cardList = ref([])
const cardToPlan = ref(false)
const planList = ref([]);
const changeCheck = ref(true);
const dayCountRef = ref('');

watch(() => planList,
    (newplanList) => {
    cardList.value = cardListRaw.value.filter(data => planList.value.reduce((acc, item) => acc && (item.cardId !== data.cardId), true))
}, {deep:true}, {immediate:true})
watch(() => cardListRaw,
    (newcardListRaw) => {
    cardList.value = cardListRaw.value.filter(data => planList.value.reduce((acc, item) => acc && (item.cardId !== data.cardId), true))
}, {deep:true}, {immediate:true})
const days = ref([]);
const checkDay = ref(0);
const filteredPlan = computed(() => planList.value.filter((item) => item.day === checkDay.value+1).sort((a, b) => a.orderNumber - b.orderNumber))
const newC = ref({
    lat: 37.5659316,
    lng: 126.9744791
})

const roomId = route.params.id; // URL에서 roomId 추출
const wsUrl = `${import.meta.env.VITE_VUE_YJS_WS_URL}`; // WebSocket 서버 URL

// const doc = new Y.Doc();// Yjs 배열 초기화

// const yCardList = doc.getArray('cardList');
// const yPlanList = doc.getArray('planList');

// WebSocket 프로바이더 초기화
const wsProvider = new WebsocketProvider(wsUrl, roomId, doc);
const visible = ref(false);

// yCardList의 변화를 감지하여 cardList 업데이트
yCardList.observe(debounce(() => {
    console.log('yCardList',yCardList._first.content.arr)
    cardList.value = yCardList.toArray();
    console.log('이벤트 발생 및 cardList 확인', cardList.value);
}, 500));

// yPlanList의 변화를 감지하여 planList 업데이트
yPlanList.observe(debounce(() => {
    console.log('yPlanList',yPlanList._first.content.arr)
    planList.value = yPlanList.toArray();
    console.log('이벤트 발생 및 planList 확인', planList.value);
}, 500));





// cardList의 변화 감지 및 처리
watch(cardList, (newVal, oldVal) => {
    // yCardList와 로컬 cardList가 서로 다를 때만 업데이트를 진행합니다.
    // 배열을 비교하기 위해 간단한 JSON 문자열 비교를 사용합니다.
    // 주의: 이 방법은 배열 내 객체 순서가 중요하며, 대규모 데이터에서는 성능 저하를 일으킬 수 있습니다.
    const yCardListArray = yCardList.toArray();
    if (JSON.stringify(newVal) !== JSON.stringify(yCardListArray)) {
        // yCardList를 새로운 값으로 업데이트하기 전에 기존의 모든 항목을 삭제합니다.
        yCardList.delete(0, yCardList.length);
        // 새로운 값으로 yCardList를 업데이트합니다.
        yCardList.push(newVal);
    }
}, { deep: true });


watch(planList, (newVal, oldVal) => {
    const yPlanListArray = yPlanList.toArray();
    if (JSON.stringify(newVal) !== JSON.stringify(yPlanListArray)) {
        yPlanList.delete(0, yPlanList.length);
        yPlanList.push(newVal);
        console.log('yPlanList', yPlanList)
    }
            // planList의 변화에 따라 days 배열을 업데이트
    days.value = Array.from({ length: dayCountRef.value }, () => []);
    days.value.forEach((d, index) => {
        const filterday = computed(() => planList.value.filter((item) => item.day === index + 1).sort((a, b) => a.orderNumber - b.orderNumber))
        d.push(...filterday.value)
    })
}, { deep: true });

// watch(changeCheck.value, (newVal, oldVal) => {
//     const yPlanListArray = yPlanList.toArray();
//     if (JSON.stringify(planList.value) !== JSON.stringify(yPlanListArray)) {
//         yPlanList.delete(0, yPlanList.length);
//         yPlanList.push(planList.value);
//         console.log('yPlanList', yPlanList)
//     }
// }, { deep: true });

// watch(
//         // planList의 변화에 따라 days 배열을 업데이트
//         days.value = Array.from({ length: dayCountRef.value }, () => []);
//     days.value.forEach((d, index) => {
//         const filterday = computed(() => planList.value.filter((item) => item.day === index + 1).sort((a, b) => a.orderNumber - b.orderNumber))
//         d.push(...filterday.value)
//     })
//     console.log('days변경 확인',days.value)
    
// )

// draggable js에 필요한 거////////////////////////////
const controlOnStart = ref(true);                   //
function clone({ name }) {                          //
    return { name }                                 //
}                                                   //
function pullFunction() {                           //
    return controlOnStart.value ? "clone" : false;  //
}                                                   //
//////////////////////////////////////////////////////



function saveCards() {
    // axios 연결
}

function goMain() {
    router.push({ name: 'mypage-myplan' })
}


function onCardMove(event, index) {
    console.log(event);
    //planList에 day추가용
    // console.log('index',index);

    const { added, removed, moved } = event;
    if (added) {
        const cardId = added.element.cardId;
        // 여행 상세 계획에 카드를 추가한 경우, 카드 목록에서 해당 카드를 제거
        const indexToRemovePlan = planList.value.findIndex(plan => plan.cardId === cardId);
        const indexToRemove = cardList.value.findIndex(card => card.cardId === cardId);

        console.log('indexToRemove',indexToRemove)
        if (indexToRemove !== -1) {
            const [cardToAdd] = cardList.value.splice(indexToRemove, 1);
            const newCard = {
                id: null,
                cardId: cardToAdd.cardId, // 여기서는 예시로 cardId만 매핑했습니다. 실제로는 모든 필요한 필드를 매핑해야 합니다.
                placeName: cardToAdd.placeName,
                placeAddress: cardToAdd.placeAddress,
                Lat: cardToAdd.Lat,
                Lng: cardToAdd.Lng,
                placeImage: cardToAdd.placeImage,
                orderNumber: added.newIndex + 1,
                day: index + 1, // 0부터 들어감 (그러므로 1 더해줘야 함)
                memo: cardToAdd.memo,
            };
            planList.value.push(newCard);
            days.value[index].forEach((item, i) => {
                const changeIndex = planList.value.findIndex(plan => plan.cardId === item.cardId);

                const newCard = { ...planList.value[changeIndex] }

                planList.value[changeIndex] = {
                id: newCard.id,
                cardId: newCard.cardId, // 여기서는 예시로 cardId만 매핑했습니다. 실제로는 모든 필요한 필드를 매핑해야 합니다.
                placeName: newCard.placeName,
                placeAddress: newCard.placeAddress,
                Lat: newCard.Lat,
                Lng: newCard.Lng,
                placeImage: newCard.placeImage,
                orderNumber: i,
                day: newCard.day, // 0부터 들어감 (그러므로 1 더해줘야 함)
                memo: newCard.memo,
            };
            })
        } else {
            planList.value[indexToRemovePlan].day = index + 1;
            days.value[index].forEach((item, i) => {
                const changeIndex = planList.value.findIndex(plan => plan.cardId === item.cardId);
                const newCard = { ...planList.value[changeIndex] }

                planList.value[changeIndex] = {
                id: newCard.id,
                cardId: newCard.cardId, // 여기서는 예시로 cardId만 매핑했습니다. 실제로는 모든 필요한 필드를 매핑해야 합니다.
                placeName: newCard.placeName,
                placeAddress: newCard.placeAddress,
                Lat: newCard.Lat,
                Lng: newCard.Lng,
                placeImage: newCard.placeImage,
                orderNumber: i,
                day: newCard.day, // 0부터 들어감 (그러므로 1 더해줘야 함)
                memo: newCard.memo,
            };
            })
        }
    }
    else if(moved){
        days.value[index].forEach((item, i) => {
            const changeIndex = planList.value.findIndex(plan => plan.cardId === item.cardId);
            const newCard = { ...planList.value[changeIndex] }

            planList.value[changeIndex] = {
            id: newCard.id,
            cardId: newCard.cardId, // 여기서는 예시로 cardId만 매핑했습니다. 실제로는 모든 필요한 필드를 매핑해야 합니다.
            placeName: newCard.placeName,
            placeAddress: newCard.placeAddress,
            Lat: newCard.Lat,
            Lng: newCard.Lng,
            placeImage: newCard.placeImage,
            orderNumber: i,
            day: newCard.day, // 0부터 들어감 (그러므로 1 더해줘야 함)
            memo: newCard.memo,
            };
        })
        // moved된 날의 전체 order를 다시 덮어씀
    }
    else if (removed) {
        days.value[index].forEach((item, i) => {
            const changeIndex = planList.value.findIndex(plan => plan.cardId === item.cardId);
            const newCard = { ...planList.value[changeIndex] }

            planList.value[changeIndex] = {
            id: newCard.id,
            cardId: newCard.cardId, // 여기서는 예시로 cardId만 매핑했습니다. 실제로는 모든 필요한 필드를 매핑해야 합니다.
            placeName: newCard.placeName,
            placeAddress: newCard.placeAddress,
            Lat: newCard.Lat,
            Lng: newCard.Lng,
            placeImage: newCard.placeImage,
            orderNumber: i,
            day: newCard.day, // 0부터 들어감 (그러므로 1 더해줘야 함)
            memo: newCard.memo,
            };
        })
        // removed된 날의 전체 order를 다시 덮어씀
    }
    
    console.log('planList에 추가', planList.value)
    changeCheck.value = !changeCheck.value
}

const changeDate = (day) => {
    checkDay.value = day
}

const moveCenter = (lat, lng) => {
    newC.value.lat = lat
    newC.value.lng = lng
}


// 카드 데이터를 가져오는 메서드
async function fetchCardList() {
    try {
        const response = await cardListGetApi(planId);
        if (response.data.dataHeader.successCode === 0) {
            const backendCardList = response.data.dataBody;
            // 백엔드로부터 받아온 세부 계획을 기반으로 card리스트를 업데이트
            cardListRaw.value = backendCardList.map(card => (
                {
                cardId: card.cardId,
                Lat: card.latitude, // 백엔드에서 제공하는 위도 정보 필드명에 맞게 수정
                Lng: card.longitude, // 백엔드에서 제공하는 경도 정보 필드명에 맞게 수정
                placeImage: card.placeImage, // 백엔드에서 제공하는 이미지 정보 필드명에 맞게 수정
                memo: card.memo,
                placeAddress: card.placeAddress,
                placeName: card.placeName
            }));

            console.log('fetch후 cardListRaw 값', cardListRaw.value)
            planStore.cardListRaw = cardListRaw
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
}

// 서치창에서 카드 추가시 다시 카드리스트 불러오기
let Added = computed(() => planStore.Added);
watch(Added, (newV, oldV) => {
    fetchCardList();
    console.log(cardListRaw.value)
}, { deep: true })


// 여행 일수를 계산하는 함수
const calculateDateDiff = (startDate, endDate) => {
    const start = new Date(startDate);
    const end = new Date(endDate);
    const diffTime = Math.abs(end - start);
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    return diffDays+1;
};

// days 배열을 초기화하는 함수
const initializeDays = (dayCount) => {
    days.value = Array.from({ length: dayCount }, () => []);
    days.value.forEach((d, index) => {
        const filterday = computed(()=>planList.value.filter((item) => item.day === index+1).sort((a, b) => a.orderNumber - b.orderNumber))
        d.push(...filterday.value)
    })
};


// `update-dates` 이벤트를 처리하는 메서드
const handleUpdateDates = ({ startDate, endDate }) => {
    // 여행 일수 계산
    const dayCount = calculateDateDiff(startDate, endDate);
    dayCountRef.value = dayCount;
    // days 배열 초기화
    initializeDays(dayCount);
};

// 해당 여행 계획의 여행 세부 계획 리스트 가져오기 메서드
async function fetchPlanDetailList() {
    try {
        console.log(yPlanList)
        const yPlanListArray = yPlanList.toArray();
        const response = await planDetailListGetApi(planId);
        if (response.data.dataHeader.successCode === 0) {
            const backendPlanDetails = response.data.dataBody;
            if (JSON.stringify(backendPlanDetails) !== JSON.stringify(yPlanListArray)) {
                planList.value = yPlanListArray
            }
            console.log('backendPlanDetails', backendPlanDetails);
            // 백엔드로부터 받아온 세부 계획을 기반으로 planList를 업데이트
            planList.value = backendPlanDetails.map(detail => (
                {
                id: detail.id,
                cardId: detail.cardId,
                Lat: detail.latitude, // 백엔드에서 제공하는 위도 정보 필드명에 맞게 수정
                Lng: detail.logitude, // 백엔드에서 제공하는 경도 정보 필드명에 맞게 수정
                placeImage: detail.placeImage, // 백엔드에서 제공하는 이미지 정보 필드명에 맞게 수정
                memo: detail.cardMemo,
                orderNumber: detail.orderNumber,
                day: detail.day,
                placeAddress: detail.placeAddress,
                placeName: detail.placeName
            }));
            console.log('fetch후 planList 값', planList.value)
            days.value.forEach((d, index) => {
                d.splice(0,d.length);
                const filterday = computed(()=>planList.value.filter((item) => item.day === index+1).sort((a, b) => a.orderNumber - b.orderNumber))
                d.push(...filterday.value)
            })
        } else {
            alert(response.data.dataHeader.resultMessage);
        }

        console.log("planDetailList 받아오기: ", planList.value);
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
}



// 여행 세부 계획 생성 OR 수정 전 해당 API 요청값에 맞게끔 변경해주는 메서드
function getPlanDetailsConvert() {
    return planList.value.map((plan) => {
        // id가 없는 경우에는 undefined로 설정하여, 서버측에서 새 엔티티로 인식하게 함
        return {
            id: plan.id,
            cardId: plan.cardId,
            orderNumber: plan.orderNumber,
            day: plan.day
        };
    });
}

// 여행 세부 계획 생성 OR 수정
async function planDetailSave() {
    try {
        const param = getPlanDetailsConvert();
        console.log('param',param)
        const response = await planDetailCreateApi(planId, param);
        if (response.data.dataHeader.successCode === 0) {
            alert("여행 계획이 저장되었습니다.");
            console.log("여행 세부 계획 생성 OR 수정", planList.value);
            await fetchPlanDetailList();
            console.log('planList', planList.value);
        } else {
            alert(response.data.dataHeader.resultMessage);
        }
    } catch (error) {
        if (error.response) {
            console.error(error);
            const errorResponse = error.response.data;
            alert(errorResponse.dataHeader.resultMessage);
            console.log('planlist',planList.value)
        } else if (error.message === 'Network Error' || error.code === 'ERR_NETWORK') {
            // 네트워크 에러 처리
            alert("서버에 연결할 수 없습니다. 네트워크 연결을 확인해주세요.");
        }
    }
}




onBeforeMount(async () => {
    await fetchCardList();
    await fetchPlanDetailList();
});


const sttOn = ref(false);

// SpeechRecognition 인스턴스를 관리할 ref 추가
const speechRecognition = ref(null);

const sttToggle = async () => {
  sttOn.value = !sttOn.value;

  if (sttOn.value) {
    // STT 활성화 시
    // SpeechRecognition 인스턴스 생성
    const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
    if (SpeechRecognition) {
      speechRecognition.value = new SpeechRecognition();
      speechRecognition.value.continuous = true; // 연속적인 입력 처리
      speechRecognition.value.lang = 'ko-KR'; // 언어 설정 (한국어)
      speechRecognition.value.interimResults = true; // 중간 결과 반환

      // 음성 인식 시작
      speechRecognition.value.start();

      // 음성 인식 결과 이벤트 처리
      speechRecognition.value.onresult = function(event) {
        const current = event.resultIndex;
        const transcript = event.results[current][0].transcript;  // 변환된 텍스트
        const text = transcript.split(" "); // 변환된 텍스트를 공백단위로 끊어서 배열로 저장
        // console.log(text);
        const lastWord = text[text.length - 1]; // 배열의 맨 마지막 요소(단어) 추출
        planStore.sttSearchText = lastWord
        console.log(lastWord); // 맨 마지막 단어 
      };

      speechRecognition.value.onerror = function(event) {
        console.error('SpeechRecognition error', event.error);
      };

      console.log("STT 시작");
    } else {
      console.error("이 브라우저는 SpeechRecognition을 지원하지 않습니다.");
    }
  } else {
    // STT 비활성화 시
    if (speechRecognition.value) {
      speechRecognition.value.stop(); // 음성 인식 중지
      speechRecognition.value = null;
      console.log("STT 중지");
    }
  }
};

// 20글자를 넘어가면 요약 ("...")
const truncateName = (name) => {
  if (name.length > 20) {
    return name.slice(0, 20) + "...";
  } else {
    return name;
  }
}
</script>

<template>
    <div class="drag-container">
        <div class="row" style="display: flex;">
            <div class="drag-list">
                <ItemTitle class="title" @update-dates="handleUpdateDates"></ItemTitle>
                

                <!-- 카드목록 -->
                <h6 style="margin-left: 11%;">카드 목록</h6>
                <div class="card-list-margin">
                    <draggable class="DragArea list-group" :list="cardList"
                        :group="{ name: 'card', pull: 'clone', put: false }" item-key="id" @change="onCardMove">
                        <template #item="{ element, index }">
                            <div >
                                <div class="list-group-item font-content">
                                    <div class="d-flex align-items-center gap-3 justify-content-center">
                                        <div class="card-card-list d-flex justify-content-start gap-3 align-items-center" id="card1">
                                            <img class="card-image" :src="element.placeImage" alt="">
                                            <div style="display: flex; flex-direction: column;">
                                                <div style="font-weight: bold;">{{ element.placeName }}</div>
                                                <div>{{ truncateName(element.placeAddress) }}</div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </template>
                    </draggable>
                </div>
            </div>
            <!-- 카드목록 -->

            <!-- 2번째 열 -->
            <div class="drag-list">
                <div style="height: 80px; padding: 1rem ; display:flex; align-items: end;">
                    <div class="d-flex align-items-center">
                        <div style="font-size: 28px;">
                            {{ planList.length }}
                        </div>
                        <div class="font-content" style="font-size: 15px;">개의 장소</div>
                    </div>
                </div>
                <div>

                    <!-- 여행 상세 계획 -->
                    <div class="plan-list-margin">
                        <div class="drag-plan-list" v-for="(fixCard, index) in days" :key="index">
                            <hr style="width: 90%; margin: 0 auto; border: 1.5px solid; opacity: 0.1; border-radius: 10cm;">
                            <h6 id="travelDay" @click="changeDate(index)">Day {{ index + 1 }}</h6>
                            <div>
                                <draggable class="DragArea list-group" :list="fixCard" :group="{ name: 'card', put: true }"
                                    item-key="id" @change="onCardMove($event, index)">
                                    <template #item="{ element, index }">
                                        <div @click="moveCenter(element.Lat, element.Lng)"
                                            class="list-group-item font-content">
                                            <div class="d-flex align-items-center gap-3 justify-content-center"
                                                style="position: relative; margin-bottom: 10px;">
                                                <div class="orderNumber">{{ index + 1 }}</div>
                                                <div class="card-detail d-flex justify-content-start gap-3 align-items-center" id="card2">
                                                    <img class="card-image" :src="element.placeImage" alt="">
                                                    <div>{{ element.placeName }}</div>
                                                </div>
                                            </div>
                                        </div>
                                    </template>
                                </draggable>
                            </div>
                        </div>
                    </div>
                    <!-- 여행 상세 계획 -->

                    <!-- 버튼 -->
                    <div class="btns">
                        <div class="btns-box">
                            <button class="btn sttBtn" @click="sttToggle()">
                                {{ sttOn ? "stt OFF" : "stt ON" }}
                            </button>
                            <button class="btn quit-btn" @click="goMain">
                                <i class="pi pi-times"></i> 종료
                            </button>
                            <button class="btn save-btn" @click="planDetailSave">
                                <i class="pi pi-check"></i> 저장
                            </button>
                        </div>
                    </div>
                    <!-- 버튼 -->

                </div>
            </div>
            <!-- 2번째 열 -->

            <div class="map">
                <!-- {{ newC }} -->
                <KaKaoMap :new-center="newC" :detail-list="filteredPlan"></KaKaoMap>
            </div>
        </div>
    </div>
</template>
    
<style scoped>

.map {
    position: relative;
}

.title {
    height: 130px;
}

.card-detail {
    width: 280px;
    border-radius: 5px;
    padding: 0.4rem;
    position: relative;
    box-shadow: 0px 3px 5px gainsboro;
}

.card-card-list {
    width: 300px;
    height: 70px;
    border-radius: 5px;
    padding: 0.4rem;
    position: relative;
    box-shadow: 0px 3px 5px gainsboro;
}

.card-image {
    width: 3rem;
    height: 3rem;
    border-radius: 5px;
}

.sidebar-active {
    display: block;
    width: 50vw;
    transition: width 1s;
    transition-timing-function: cubic-bezier(0, 0, 0.2, 1);
}

.sidebar-active {
    display: block;
    width: 0vw;
    transition: width 1s;
    transition-timing-function: cubic-bezier(0, 0, 0.2, 1);
}

.tab {
    position: fixed;
    right: 0px;
    top: 5rem;
}

.drag-container {
    width: 100vw;
    height: 100vh;
}

.drag-list {
    height: 100vh;
    min-width: 380px;
    border-left: rgba(0, 0, 0, 0.1) 1px solid;
    background-color: #fff;
}
.card-list-margin{
    position: absolute;
    top: 100x;
    overflow: auto;
    width: 380px;
    height: calc(100vh - 146.8px - 73px);
}
.plan-list-margin {
    position: absolute;
    top: 80px;
    overflow: auto;
    width: 380px;
    height: calc(100vh - 120px);
    margin-top: 1rem;
    padding-top: 1rem;
}

.btns {
    background-color: #fff;
    position: absolute;
    bottom: 0px;
    width: 380px;
    height: 50px;
}

.btns-box {
    position: relative;
}

.btn {
    position: absolute;
    bottom: 2rem;
    width: 70px;
    height: 30px;
    text-align: center;
    z-index: 10;
    padding-top: 3px;
    
}

.quit-btn {
    top: 5px;
    right: 10px;
    border: red 2.5px solid;
    border-radius: 5cm;
    background-color: #fff;
    color: red;
    transition: transform 0.3s ease;

}
.quit-btn:hover {
    transform: scale(1.05);
}

.save-btn {
    top: 5px;
    right: 90px;
    border: rgba(0, 0, 0, 0.1) 2.5px solid;
    border-radius: 5cm;
    background-color: #3498DB;
    color: #fff;
    transition: transform 0.3s ease;
}
.save-btn:hover {
  transform: scale(1.05);
}
.save-btn:hover {
  transform: scale(1.05);
}

.sttBtn {
    top: 5px;
    right: 170px;
    border: rgba(0, 0, 0, 0.1) 2.5px solid;
    border-radius: 5cm;
    background-color: #FFC0CB;
    color: #fff;
    transition: transform 0.3s ease;
}
.sttBtn:hover {
  transform: scale(1.05);
}





.drag-plan-list {
    margin-bottom: 15%;
    z-index: 2;
}

.orderNumber {
    background-color: #3498DB;
    color: #fff;
    width: 20px;
    height: 20px;
    font-size: 15px;
    text-align: center;
    font-weight: bold;
    border-radius: 50%;
}
</style>
    
<style>
.overlay {
    background-color: white;
    border-radius: 5px;
    box-shadow: 0 0 5px rgba(0, 0, 0, 0.8);
}

.marker-number {
    position: relative;
    color: #fff;
    left: -1px;
    top: 2px;
    width: 28px;
    height: 28px;
    background-color: #3498DB;
    /* border: black solid 1px; */
    border-radius: 50%;
    font-size: 20px;
    font: bolder;
    display: inline-block;
    text-align: center;
}

#travelDay {
  cursor: pointer;
  margin-left: 6%;
  transition: transform 0.3s ease;
}
#travelDay:hover {
  transform: scale(1.05);
}

#card1 {
  cursor: pointer;
  transition: transform 0.3s ease;
}
#card1:hover {
  transform: scale(1.05);
}

#card2 {
  cursor: pointer;
  transition: transform 0.3s ease;
}
#card2:hover {
  transform: scale(1.05);
}

</style>