<template>
  <div class="card p-fluid" id="box">
    <div class="container; card p-fluid" id="signUpBox">
      <h1 id="signUpTitle">SignUp</h1>
      <form @submit.prevent="signUp" id="signUpForm">
        <div v-if="userPhotoUrl" id="profileImg">
          <img :src="userPhotoUrl" alt="프로필 이미지">
        </div>
        <div v-else></div>
        <div class="box, card p-fluid" id="userEmailInput">
            <input type="userEmail" id="userEmail" v-model.trim="userEmail" placeholder="이메일">
        </div>
        <div class="box, card p-fluid" id="userNameInput">
            <input type="userName" id="userName" v-model.trim="userName" placeholder="이름">
        </div>
        <div class="box, card p-fluid" id="userNicknameInput">
            <input type="userNickname" id="userNickname" v-model.trim="userNickname" placeholder="닉네임">
        </div>
        <div class="box, card p-fluid" id="password1Input">
            <input type="password" id="password1" v-model.trim="password1" placeholder="비밀번호">
        </div>
        <div class="box, card p-fluid" id="password2Input">
            <input type="password" id="password2" v-model.trim="password2" placeholder="비밀번호 재입력">
        </div>
<!--  -->
        <div class="card p-fluid" id="profilePhoto">
        <p id="profilePhotoGuide">프로필 사진</p>
        <hr id="separator">
            <div class="box, card p-fluid" id="userPhotoInput">
                <input type="file" id="userPhoto" accept="image/*" @change="handlePhotoUpload">
            </div>
      </div>
<!--  -->

            <input class="box, card p-fluid"  id="signUpSubmit" type="submit" value="가입하기">
      </form>




    </div>
  </div>
</template>

<script setup>
  import { ref } from 'vue';
  import { useAccountsStore } from '@/stores/accountsStore.js';

  const accountsStore = useAccountsStore()

  const userEmail = ref(null);
  const userName = ref(null);
  const userNickname = ref(null);
  const password1 = ref(null);
  const password2 = ref(null);
  const userPhoto = ref(null);
  const userPhotoUrl = ref('');

  const signUp = function () {
      const info = {
          userEmail: userEmail.value,
          userName: userName.value,
          userNickname: userNickname.value,
          password1: password1.value,
          password2: password2.value,
          userPhoto: userPhoto.value,
      }
      accountsStore.signUp(info)
  }

  // 파일 업로드를 위한 이벤트 핸들러
  const handlePhotoUpload = (event) => {
      const file = event.target.files[0];
      // 파일을 FormData에 추가하여 전송 가능
      userPhoto.value = file;
      userPhotoUrl.value = URL.createObjectURL(file);
  }
</script>



<style scoped>
  #box {
  display: flex;
  justify-content: center;
  }

  #signUpBox {
    background-color: #FFFFFF;
    border: 1px solid rgba(52, 152, 219, 0.5);
    width: 400px;
    height: 50%;
    padding: 40px;

    display: flex;
    flex-direction: column;
    justify-content: space-around;
    align-items: center;
  }
  #signUpTitle {
    color: #3498db;
    font-weight: bold;
  }
  #signUpForm {
    display: flex;
    flex-direction: column;
    align-items: center;
  }
  #profileImg {
    border-radius: 50%;
    border: 1px solid rgba(52, 152, 219, 0.5);
    background-color: #FFFFFF;
    width: 250px;
    height: 250px;
    display: flex;
    align-items: center;
    justify-content: center;
    position: relative; /* 상대 위치 지정 */
    overflow: hidden; /* 원 밖으로 넘치는 부분 숨김 */
    margin-top: 10px;
    margin-bottom: 20px;
  }

  #profileImg img {
    max-width: none; /* 이미지 너비를 원본 크기로 설정 */
    max-height: none; /* 이미지 높이를 원본 크기로 설정 */
    width: 100%; /* 이미지 너비를 자동으로 조정 */
    height: auto; /* 이미지 높이를 자동으로 조정 */
    border-radius: 50%;
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%); /* 이미지를 수평 및 수직 가운데 정렬 */
  }
  #userEmail, #userName, #userNickname, #password1, #password2, #userPhoto {
    height: 35px;
    width: 250px;
  }
  #idInput, #userEmailInput, #userNameInput, #userNicknameInput, #password1Input, #password2Input {
    background-color: rgba(245, 245, 245, 0.1);
    width: 90%;
    display: flex;
    align-items: center;
    border: 1px solid rgba(52, 152, 219, 0.5);
    height: 40px;
    margin-bottom: 10px;
  }
  #separator {
    margin-top: 3px;
    border: none;
    border-top:  1px solid rgba(52, 152, 219, 0.5);
    border-radius: 10px;
  }

  #profilePhoto {
    background-color: #FFFFFF;
    border: 1px solid rgba(52, 152, 219, 0.5);
    color: rgba(0, 0, 0, 0.5);
    font-weight: bold;
    width: 100%;
    padding: 5px;
    margin-top: 20px;
  }
  #profilePhotoGuide {
    text-align: center;
    padding-top: 5px;
    margin: 0;
  }
  #userPhotoInput {
    border: 1px solid rgba(52, 152, 219, 0.5);
    display: flex;
    justify-content: center; /* 수평 가운데 정렬 */
    align-items: center;
    width: 90%; /* 현재 가로 너비 설정에 따라 조정 */
    margin: 0 auto; /* 가운데 정렬을 위한 외부 여백 설정 */
    margin-bottom: 10px;
  }
  #signUpSubmit {
    text-align: center;
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
    line-height: 0px;
  }

</style>