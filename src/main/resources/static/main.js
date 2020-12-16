fetch("/accounts/list")
  .then(response => response.json())
  .then(data => {
    let accountTable = document.getElementById("account-table");
    data.forEach((account, i) => {
      let row = accountTable.insertRow();

      let headRenderCell = row.insertCell();
      let imageElement = document.createElement("img");
      imageElement.src = "https://crafatar.com/avatars/" + account.uuid + "?overlay=true&size=48";
      headRenderCell.appendChild(imageElement);

      let nameCell = row.insertCell();
      nameCell.classList.add("name-cell");
      let usernameElement = document.createElement("p");
      usernameElement.innerHTML = account.username;
      usernameElement.classList.add("username-element");
      nameCell.appendChild(usernameElement);
      let uuidElement = document.createElement("p");
      uuidElement.innerHTML = account.uuid;
      uuidElement.classList.add("uuid-element");
      nameCell.appendChild(uuidElement);

      let buttonCell = row.insertCell();
      let buttonElement = document.createElement("button");
      buttonElement.classList.add("button-element");
      buttonElement.innerHTML = "Connect";
      buttonElement.addEventListener("click", () => onButtonClick(i));
      buttonCell.appendChild(buttonElement);
    });
  });

const socket = new WebSocket("ws://" + window.location.hostname + (window.location.port ? ":" + window.location.port : ""));

function onButtonClick(i) {
  let msg = {
    type: "connect",
    accountIndex: i
  };
  socket.send(JSON.stringify(msg));
}
