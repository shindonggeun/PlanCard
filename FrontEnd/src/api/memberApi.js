import { localAxios } from "@/util/http-commons";

const local = localAxios();

async function memberLoginApi(param, success, fail) {
    await local.post(`/member/login`, param).then(success).catch(fail);
}

async function startSocialLoginApi(oAuthDomain) {
    return await local.get(`/oauth/${oAuthDomain}`);
}

async function finishSocialLoginApi(oAuthDomain, code) {
    return await local.get(`/oauth/${oAuthDomain}/login`, { params: { code } });
}

export {
    memberLoginApi,
    startSocialLoginApi,
    finishSocialLoginApi,
};