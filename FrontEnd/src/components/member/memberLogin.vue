<template>
  <div class="card p-fluid" id="box">
    <div class="container card p-fluid" id="logInBox">
      <form @submit.prevent="login">
        <div class="box card p-fluid" id="idInput">
          <img src="/아이디 아이콘.png" alt="아이디" id="idIcon">
          <input type="text" id="memberEmail" v-model.trim="memberEmail" placeholder="ID">
        </div>
        <div class="box card p-fluid" id="pwInput">
          <img src="/비밀번호 아이콘.png" alt="비밀번호" id="pwIcon">
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

const router = useRouter();


const memberEmail = ref('');
const memberPassword = ref('');

const login = async () => {
  const param = {
    email: memberEmail.value,
    password: memberPassword.value
  };

  try {
    await memberLoginApi(param,
      (response) => {
        // 서버에서 성공적인 응답을 받은 경우
        if (response.data.dataHeader.successCode === 0) {
          console.log(response.data.dataBody);
          router.push({ name: 'main' });
        } else {
          // 로그인 실패 처리
          alert(response.data.dataHeader.resultMessage);
        }


        // router.push({ name: 'main' });  // 로그인 성공 후 메인 페이지로 이동
      },
      (err) => {
        console.error(err);
        alert('로그인에 실패했습니다.');
      }
    );
  } catch (err) {
    console.error(err);
    alert("로그인에 실패했습니다.");
  }
}

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