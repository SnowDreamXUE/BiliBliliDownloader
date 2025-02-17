const state = {
    videoUrl: '',
    bgUrl: '',
    avid: '',
    p: '',
    pages: [],          // 所有分集信息
    selectedPages: [],  // 选中的分集索引
}

const mutations = {
    setVideoUrl(state, videoUrl) {
        state.videoUrl = videoUrl
    },
    setBgUrl(state, bgUrl) {
        state.bgUrl = bgUrl
    },
    setAvid(state, avid) {
        state.avid = avid
    },
    setP(state, p) {
        state.p = p
    },
    setPages(state, pages) {
        state.pages = pages
    },
    setSelectedPages(state, selectedPages) {
        state.selectedPages = selectedPages
    },
    setDownloadUrls(state, downloadUrls) {
        state.downloadUrls = downloadUrls
    },

    // 删除选中的分集
    removeSelectedPage(state, cid) {
        // 从 selectedPages 中移除对应的分集索引
        state.selectedPages = state.selectedPages.filter(selectedCid => selectedCid !== cid);

        // 如果需要同时从 pages 中移除分集数据
        state.pages = state.pages.filter(page => page.cid !== cid);
    },
}

const actions = {
    setVideoUrl(context, videoUrl) {
        context.commit('setVideoUrl', videoUrl)
    },
    setBgUrl(context, bgUrl) {
        context.commit('setBgUrl', bgUrl)
    },
    setAvid(context, avid) {
        context.commit('setAvid', avid)
    },
    setP(context, p) {
        context.commit('setP', p)
    },
    setPages(context, pages) {
        context.commit('setPages', pages)
    },
    setSelectedPages(context, selectedPages) {
        context.commit('setSelectedPages', selectedPages)
    },
    setDownloadUrls(context, downloadUrls) {
        context.commit('setDownloadUrls', downloadUrls)
    },

    removeSelectedPage(context, cid) {
        context.commit('removeSelectedPage', cid)
    },

    saveData(context, data) {
        context.commit('setBgUrl', data.bgUrl)
        context.commit('setAvid', data.avid)
        context.commit('setP', data.p)
        context.commit('setPages', data.pages)
        context.commit('setSelectedPages', data.selectedPages)
        context.commit('setDownloadUrls', data.downloadUrls)
    },

    clearData(context) {
        context.commit('setBgUrl', '')
        context.commit('setAvid', '')
        context.commit('setP', '')
        context.commit('setPages', [])
        context.commit('setSelectedPages', [])
        context.commit('setDownloadUrls', [])
    },
}

export default {
    namespaced: true,
    state,
    mutations,
    actions,
}