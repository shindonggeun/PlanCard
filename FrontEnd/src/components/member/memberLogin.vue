<template>
  <div class="card p-fluid" id="box">
    <div class="container; card p-fluid" id="logInBox">
      <h1 id="logInTitle">LogIn</h1>
      <form @submit.prevent="logIn()" id="logInForm">
        <div class="box, card p-fluid" id="idInput">
          <img src="/아이디 아이콘.png" alt="아이디" id="idIcon">
          <label for="userId"></label>
          <input type="text" id="userId" v-model.trim="userId" placeholder="ID">
        </div>
        <div class="box, card p-fluid" id="pwInput">
          <img src="/비밀번호 아이콘.png" alt="비밀번호" id="pwIcon">
          <label for="password"></label>
          <input type="password" id="password" v-model.trim="password" placeholder="PW">
        </div>
        <div class="box, card p-fluid" id="loginSubmit">
          <input type="submit" value="LogIn">
        </div>
      </form> 
      <div id="etc1">
          <p id="signUpGuide">아직 회원이 아니신가요?</p>
          <button id="signUpBtn" @click="goSignUp()">[회원가입]</button>
      </div>
      <div id="etc2">
          <p id="pwSearchGuide">비밀번호를 잊어버렸나요?</p>
          <button id="pwSearchBtn">[비밀번호 찾기]</button>
      </div>
      <div class="card p-fluid" id="otherLogin">
        <p id="otherLoginGuide">다른 방법으로 로그인</p>
        <hr id="separator">
        <div id="otherLoginBtn">
          <img src="/네이버 로그인 버튼.png" alt="네이버로그인" id="loginLogoImg" @click="accountsStore.naverLogin()">
          <img src="/카카오 로그인 버튼.png" alt="카카오로그인" id="loginLogoImg" @click="accountsStore.kakaoLogin()">
        </div>
      </div>
    </div>
  </div>
</template>



<script setup>
  import { ref } from 'vue';
  import { useAccountsStore } from '@/stores/accountsStore.js';
  import { useRouter } from 'vue-router';

  const router = useRouter()
  const accountsStore = useAccountsStore()

  const userId = ref(null)
  const password = ref(null)

  const logIn = function () {
    const info = {
        userId: userId.value,
        password: password.value
    }
    accountsStore.logIn(info)
  }

  const goSignUp = function () {
    router.push({name: 'member-signup'});
  }
</script>



<style scoped>
  #box {
  display: flex;
  justify-content: center;
  }
  #logInForm {
    display: flex;
    flex-direction: column;
    align-items: center;
  }
  #logInBox {
    border: 1px solid rgba(52, 152, 219, 0.5);

    background-color: #FFFFFF;
    width: 400px;
    height: 50%;
    padding: 40px;

    display: flex;
    flex-direction: column;
    justify-content: space-around;
    align-items: center;
  }
  #logInTitle {
    color: #3498db;
    font-weight: bold;
    padding-bottom: 40px;
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
    margin-right: 10px;
    margin-left: -15px;
  }
  #userId {
    height: 35px;
    width: 250px;
  }
  #password {
    height: 35px;
    width: 250px;
  }
  #idInput {
    background-color: rgba(245, 245, 245, 0.1);
    width: 90%;
    display: flex;
    align-items: center;
    border: 1px solid rgba(52, 152, 219, 0.5);
    height: 40px;
    margin-bottom: 10px;
  }
  #pwInput {
    background-color: rgba(245, 245, 245, 0.1);
    width: 90%;
    display: flex;
    align-items: center;
    border: 1px solid rgba(52, 152, 219, 0.5);
    height: 40px;
    margin-bottom: 10px;
  }
  #loginSubmit {
    width: 90%;
    display: flex;
    align-items: center;
    justify-content: center;
    border: 1px solid rgba(0, 0, 0, 0.1);
    background-color: #3498db;
    color: #FFFFFF;
    font-weight: bold;
    font-size: medium;
    height: 20px;
    padding: 20px;
  }

  #etc1 {
    display: flex;
    margin-top: 10px;
  }
  #signUpGuide {
    color: rgba(0, 0, 0, 0.3);
    font-weight: bold;
    margin: 0;
    margin-right: 10px;
  }
  #signUpBtn {
    font-weight: bold;
    color: #3498db;
  }
  #etc2 {
    display: flex;
  }
  #pwSearchGuide {
    color: rgba(0, 0, 0, 0.3);
    font-weight: bold;
    margin: 0;
    margin-right: 10px;
  }
  #pwSearchBtn {
    font-weight: bold;
    color: #3498db;
  }

  #otherLogin {
    background-color: #FFFFFF;
    color: rgba(0, 0, 0, 0.5);
    font-weight: bold;
    width: 100%;
    padding: 5px;
    margin-top: 20px;
    border: 1px solid rgba(52, 152, 219, 0.5);
  }
  #otherLoginGuide {
    text-align: center;
    padding-top: 5px;
    margin: 0;
  }
  #separator {
    margin-top: 3px;
    border: none;
    border: 1px solid rgba(52, 152, 219, 0.5);
    border-radius: 10px;
  }
  #otherLoginBtn {
    display: flex;
    justify-content: space-around;
    width: 100%;
  }
  #loginLogoImg {
    border: 1px solid rgba(0, 0, 0, 0.1);
    width: 130px;
    margin-right: 5px;
    margin-left: 5px;
    margin-bottom: 5px;
  }
</style>