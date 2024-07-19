function getUserProfitRow(userProfit) {
    return `
        <a class="coins__item" th:href="#">
            <div class="coins__item-currency">
                <span>${userProfit["username"]}</span>
            </div>
            <div class="coins__item-last"></div>
            <div class="coins__item-change">${userProfit["profit"]}%</div>
        </a>
    `;
}

function updateUserProfits() {
    const $userProfitList = $('#user-profits-list')

    $.ajax({
        url: "../api/user/random-profits",
        method: "GET",
        success: function(data) {
            $userProfitList.empty();
            data.forEach((userProfit) => $userProfitList.append(getUserProfitRow(userProfit)))
        },
        error: (error) => console.log("Error fetching user profits:", error)
    })
}


$(document).ready(() => {
    updateUserProfits()
})