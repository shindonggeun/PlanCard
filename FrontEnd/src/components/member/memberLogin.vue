<template>
  <div class="card p-fluid" id="box">
    <div class="container; card p-fluid" id="logInBox">
      <h1 id="logInTitle">Login</h1>
      <form @submit.prevent="login()" id="logInForm">
        <div class="box, card p-fluid" id="idInput">
          <img src="/아이디 아이콘.png" alt="아이디" id="idIcon">
          <label for="memberEmail"></label>
          <input type="text" id="memberEmail" v-model.trim="memberEmail" placeholder="EMAIL">
        </div>
        <div class="box card p-fluid" id="pwInput">
          <img src="/비밀번호 아이콘.png" alt="비밀번호" id="pwIcon">
          <label for="memberPassword"></label>
          <input type="password" id="memberPassword" v-model.trim="memberPassword" placeholder="PW">
        </div>
        <div class="box card p-fluid" id="loginSubmit">
          <input type="submit" style="display: inline-block;" value="LogIn">
        </div>
      </form> 
      <div>
        <p>아직 회원이 아니신가요?</p>
        <button @click="goSignUp">[회원가입]</button>
      </div>
    </div>
  </div>
</template>



<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { memberLoginApi } from "@/api/memberApi";

const router = useRouter()

const memberEmail = ref('');
const memberPassword = ref('');

const login = async () => {
  const param = {
    email: memberEmail.value,   // userId는 이메일 필드에 대응
    password: memberPassword.value
  };

  try {
    await memberLoginApi(param,
      (response) => {
        if (response.data.dataHeader.successCode === 0) {
          router.push({ name: 'main' });  // 로그인 성공 후 메인 페이지로 이동  
        } else {
          // 로그인 실패 처리 메시지
          alert(response.data.dataHeader.resultMessage);
        }
      }
    );
  } catch (err) {
    console.error(err);
    alert("로그인 과정 중 문제가 발생했습니다.");
  }
};

const goSignUp = function () {
  router.push({ name: 'member-signup' });
}
</script>



<style scoped>
  #box {
  display: flex;
  justify-content: center;
  }
  #logInBox {
    background-color: #EAF9FF;
    width: 350px;
    height: 50%;
  }
  #idIcon {
    width: 30px;
    height: 30px;
    margin-right: 10px;
    margin-left: -15px;
  }
  #pwIcon {
    width: 30px;
    height: 30px;
    margin-top: -15px;
    margin-right: 10px;
  }
  #username {
    height: 35px;
    width: 195px;
  }
  #password {

  }
  #idInput {
    border: 1px solid rgba(0, 0, 0, 0.1);
    height: 40px;
    align-items: center; /* 하위 요소들을 수직 가운데 정렬. */
    margin-bottom: 10px;
  }
  #pwInput {
    border: 1px solid rgba(0, 0, 0, 0.1);
    height: 40px;
    align-items: center; /* 하위 요소들을 수직 가운데 정렬. */
    margin-bottom: 10px;
  }
  #loginSubmit {
    text-align: center;
    background-color: #3498db;
    color: #FFFFFF;
  }
</style>