// Je récupère les zones des joueurs 
const topPlayer = document.querySelector(".top-player");
const bottomPlayer = document.querySelector(".bottom-player");
// Je récupère les chrono de chaque zone
const topTimer = document.getElementById("chrono-top-player");
const bottomTimer = document.getElementById("chrono-bottom-player");
// On déclare les variables minutes et secondes du top-player
let topMinutes = 5;
let topSeconds = 0;
// On déclare les variables minutes et secondes du bottom-player
let bottomMinutes = 5;
let bottomSeconds = 0;

let activePlayer = "top";

let timerInterval = null;

// Fonction pour formater les secondes avec deux chiffres
function formatSeconds(seconds) {
    if (seconds < 10) {
        return "0" + seconds;
    } else {
        return seconds.toString();
    }
}

topSeconds = formatSeconds(topSeconds);
bottomSeconds = formatSeconds(bottomSeconds);

topTimer.textContent = topMinutes + " : " + topSeconds;
bottomTimer.textContent = bottomMinutes + " : " + bottomSeconds;

play()

function togglePlayer() {
    if (activePlayer === "top") {
        activePlayer = "bottom"
    } else {
        activePlayer = "top"
    }
    play()
}

topPlayer.addEventListener("click", () => {
    if (activePlayer === "top") {
        togglePlayer();
    }
})
bottomPlayer.addEventListener("click", () => {
    if (activePlayer === "bottom") {
        togglePlayer();
    }
})

function startTimer() {
    if (activePlayer === "top") {
        topSeconds--;
        if (topSeconds < 0) {
            topMinutes--;
            topSeconds = 59;
        }
        topTimer.textContent = topMinutes + " : " + formatSeconds(topSeconds);
        if (topMinutes === 0 && topSeconds === 0) {
            clearInterval(timerInterval);
            alert("Top player a perdu !");
        }
    }

    if (activePlayer === "bottom") {
        bottomSeconds--;
        if (bottomSeconds < 0) {
            bottomMinutes--;
            bottomSeconds = 59;
        }
        bottomTimer.textContent = bottomMinutes + " : " + formatSeconds(bottomSeconds);
        if (bottomMinutes === 0 && bottomSeconds === 0) {
            clearInterval(timerInterval);
            alert("Bottom player a perdu !");
        }
    }
}

// Pour démarrer le chrono du joueur actif :
function play() {
    clearInterval(timerInterval); // On stoppe le chrono précédent
    timerInterval = setInterval(startTimer, 1000); // On déclenche le tick toutes les secondes
}
