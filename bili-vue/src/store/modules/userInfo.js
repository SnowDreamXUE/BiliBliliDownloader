const state = {
    uname: '',
    face: '',
}

const mutations = {
    setUname(state, uname) {
        state.uname = uname
    },
    setFace(state, face) {
        state.face = face
    },
}

const actions = {
    setUname(context, uname) {
        context.commit('setUname', uname)
    },
    setFace(context, face) {
        context.commit('setFace', face)
    },
    setData(context, data) {
        context.commit('setUname', data.uname)
        context.commit('setFace', data.face)
    },
    removeUname(context) {
        context.commit('setUname', '')
    },
    removeFace(context) {
        context.commit('setFace', '')
    },
    clearData(context) {
        context.commit('setUname', '')
        context.commit('setFace', '')
    }
}

export default {
    namespaced: true,
    state,
    mutations,
    actions
}