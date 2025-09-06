const AdminPage = (() => {
    // 1. 상태 관리 (State)
    const state = {
        token: null,
        challenges: []
    };
    const API_TOKEN_KEY = 'admin-api-token';

    // 2. UI 요소 캐싱 (UI Elements)
    const ui = {
        authContainer: document.getElementById('auth-container'),
        challengeContainer: document.getElementById('challenge-container'),
        tokenInput: document.getElementById('token-input'),
        authButton: document.getElementById('auth-button'),
        challengeListContainer: document.getElementById('challenge-list-container')
    };

    // 3. API 통신 (API Layer)
    const api = {
        async _fetch(url, options = {}) {
            if (!state.token) {
                throw new Error('인증 토큰이 없습니다.');
            }
            const headers = {...options.headers, 'Authorization': `Bearer ${state.token}`, 'Content-Type': 'application/json'};
            const response = await fetch(url, {...options, headers});

            if (response.status === 401 || response.status === 403) {
                methods.clearToken();
                throw new Error('인증에 실패했습니다. 새로운 토큰을 입력해주세요.');
            }
            if (!response.ok) {
                throw new Error('API 요청에 실패했습니다.');
            }
            const contentType = response.headers.get("content-type");
            return contentType?.includes("application/json") ? response.json() : null;
        },
        getPendingChallenges() {
            return this._fetch('/api/v1/admin/challenges/pending');
        },
        approveChallenge(id) {
            return this._fetch(`/api/v1/admin/challenges/${id}/approve`, {method: 'POST'});
        },
        rejectChallenge(id) {
            return this._fetch(`/api/v1/admin/challenges/${id}/reject`, {method: 'POST'});
        }
    };

    // 4. 메서드 (Methods)
    const methods = {
        clearToken() {
            state.token = null;
            localStorage.removeItem(API_TOKEN_KEY);
            methods.render();
        },
        setToken(token) {
            state.token = token;
            localStorage.setItem(API_TOKEN_KEY, token);
            methods.render();
        },
        async loadChallenges() {
            try {
                const rsp = await api.getPendingChallenges();
                state.challenges = rsp.data.challenges;
                methods.renderChallenges();
            } catch (error) {
                console.error(error);
                ui.challengeListContainer.innerHTML = `<p>${error.message}</p>`;
            }
        },
        async handleAction(id, action) {
            const actionText = action === 'approve' ? '승인' : '반려';
            try {
                if (action === 'approve') {
                    await api.approveChallenge(id);
                } else {
                    await api.rejectChallenge(id);
                }
                alert(`챌린지가 성공적으로 ${actionText}되었습니다.`);
                state.challenges = state.challenges.filter(c => c.dailyMemberChallengeId !== id);
                methods.renderChallenges();
            } catch (error) {
                console.error(error);
                alert(`작업 중 오류가 발생했습니다: ${error.message}`);
            }
        },
        renderChallenges() {
            ui.challengeListContainer.innerHTML = '';
            if (state.challenges.length === 0) {
                ui.challengeListContainer.innerHTML = '<p>승인 대기 중인 챌린지가 없습니다.</p>';
                return;
            }
            state.challenges.forEach(c => {
                const item = document.createElement('div');
                item.className = 'challenge-item';
                item.innerHTML = `
                    <div class="challenge-info">
                        <p><strong>챌린지 아이디:</strong> ${c.dailyMemberChallengeId}</p>
                        <p><strong>유저:</strong> ${c.userNickname}</p>
                        <p><strong>챌린지:</strong> ${c.challengeContent}</p>
                    </div>
                    <div class="challenge-image">
                        <img src="${c.imageUrl}" alt="챌린지 인증 사진">
                    </div>
                    <div class="challenge-actions">
                        <button class="approve-btn" data-id="${c.dailyMemberChallengeId}">승인</button>
                        <button class="reject-btn" data-id="${c.dailyMemberChallengeId}">반려</button>
                    </div>`;
                ui.challengeListContainer.appendChild(item);
            });
        },
        render() {
            if (state.token) {
                ui.authContainer.classList.add('hidden');
                ui.challengeContainer.classList.remove('hidden');
                methods.loadChallenges();
            } else {
                ui.authContainer.classList.remove('hidden');
                ui.challengeContainer.classList.add('hidden');
            }
        },
        bindEvents() {
            ui.authButton.addEventListener('click', () => {
                const tokenValue = ui.tokenInput.value.trim();
                if (tokenValue) {
                    methods.setToken(tokenValue);
                    ui.tokenInput.value = '';
                } else {
                    alert('토큰을 입력해주세요.');
                }
            });
            ui.challengeListContainer.addEventListener('click', (e) => {
                const target = e.target;
                const id = Number(target.dataset.id);
                if (target.classList.contains('approve-btn')) {
                    methods.handleAction(id, 'approve');
                } else if (target.classList.contains('reject-btn')) {
                    methods.handleAction(id, 'reject');
                }
            });
        },
        init() {
            state.token = localStorage.getItem(API_TOKEN_KEY);
            methods.bindEvents();
            methods.render();
        }
    };

    // 5. Public Interface
    return {
        init: methods.init
    };
})();

document.addEventListener('DOMContentLoaded', AdminPage.init);
