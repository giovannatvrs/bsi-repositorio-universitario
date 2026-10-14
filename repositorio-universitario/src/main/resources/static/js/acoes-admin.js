function ativarPromocaoUsuarios(){
    document.querySelectorAll('.botao-tornar-usuario-moderador').forEach(button =>{
       button.addEventListener('click', async () =>{
            const userId = button.getAttribute('data-id');
            try{
                const response = await fetch(`/promover/${userId}`, {
                    method: "PUT",
                    credentials: "include"
                });
                if(!response.ok){
                    throw new Error(`Erro HTTP: ${response.status}`);
                }
                let message = "Usuário(a) promovido com sucesso";
                mostrarNotificacao(message, "success");
                setTimeout(() => {window.location.reload();

                }, 3000);
                
            }catch(error){
                let message = "Falha em promover usuário para moderador";
                mostrarNotificacao(message, "error");
                console.error("Falha em promover usuário para moderador", error);
            }
        });
    });
}

function ativarDesligamentoModerador(){
    document.querySelectorAll('.botao-retirar-papel-moderador').forEach(button =>{
        button.addEventListener('click', async () =>{
            const userId = button.getAttribute('data-id');
            try{
                const response = await fetch(`/retirar-papel/${userId}`, {
                   method: 'PUT',
                   credentials: "include"
                });
                if(!response.ok){
                    throw new Error(`Erro HTTP: ${response.status}`);
                }
                let message = "Papel de moderador retirado do usuário com sucesso";
                mostrarNotificacao(message, "success");
                setTimeout(() => {window.location.reload();

                }, 3000);
            }catch(error){
                let message = "Erro em retirar papel do moderador";
                mostrarNotificacao(message, "error");
                console.error("Erro em retirar papel do moderador", error);
            }
        })
    });
}


function mostrarNotificacao(message, type) {
    const notificacao = document.querySelector(".notificacao");
    notificacao.textContent = message;
    notificacao.className = `notificacao ${type}`;
    notificacao.style.display = "block";

    setTimeout(() => {
        notificacao.style.display = "none";
    }, 3000);
}

   async function listarModeradores(pagina){
        paginaAtual = pagina;
        try{
            const response = await fetch(`/moderadores?page=${pagina}`, {
                method: 'GET',
                credentials: "include"
            });
            if(!response.ok){
                throw new Error(`Erro HTTP: ${response.status}`);
            }
            const data = await response.json();

            let info = '';

            if (!data.content || data.content.length === 0) {
                info = `
                    <tr>
                        <td colspan="5" style="text-align: center; padding: 20px; color: #666;">
                            Nenhum moderador no momento.
                        </td>
                    </tr>
                `;
            }
            else{
                data.content.forEach(function (usuario){
                    info = `<tr><td><div class="informacoes-perfil"><img src="${usuario.url_foto}">${usuario.nome}</div></td>
                    <td>${usuario.email}</td><td><button class="botao-retirar-papel-moderador" data-id="${usuario.id}">Tirar papel de moderador</button></td></tr>`
                })
            }

            document.getElementById("lista-moderadores").innerHTML = info;
            ativarDesligamentoModerador();
            let botoesPagina = '';
            for(let i = 0; i < data.totalPages; i++){
                botoesPagina += `<button onclick="listarModeradores(${i})" class="${i === paginaAtual ? 'ativa' : ''}">${i+1}</button>`
            }
            document.getElementById("paginacao-moderadores").innerHTML = botoesPagina;
        }catch (error){
            console.error("Falha em carregar os dados dos moderadores", error);
        }
    }
    document.addEventListener("DOMContentLoaded", listarModeradores(0));

async function listarUsuarios(pagina){
        paginaAtual = pagina;
        try{
            const response = await fetch(`/usuarios?page=${pagina}`,{
                credentials: "include"
            });
            if(!response.ok){
                throw new Error(`Erro HTTP: ${response.status}`);
            }
            let data = await response.json();
            let info = '';
            console.log(data);
            data.content.forEach(function(usuario){
                let papel = '';
                switch(usuario.funcao){
                    case "ADMINISTRADOR":
                        papel = 'Administrador(a)';
                        break;
                    case "MODERADOR":
                        papel = 'Moderador(a)';
                        break;
                    case "USUARIO":
                        papel = 'Usuário(a)';
                        break;
                }

                info +=`<tr><td><div class="informacoes-perfil"><img src="${usuario.url_foto}">${usuario.nome}</div></td>
            <td>${usuario.email}</td><td>${papel}</td>

            <td class="alterar-usuario"> ${papel === "Usuário(a)" ? `<button class="botao-tornar-usuario-moderador" data-id="${usuario.id}">Tornar usuário moderador</button>` : ``}</td></tr>`

            })
            document.getElementById("lista-usuarios").innerHTML=info;
            ativarPromocaoUsuarios();
            let botoesPagina = '';
            for(let i = 0; i < data.totalPages; i++){
                botoesPagina += `<button onclick="listarUsuarios(${i})" class="${i === paginaAtual ? 'ativa' : ''}">${i+1}</button>`;
            }
            document.getElementById("paginacao-usuarios").innerHTML = botoesPagina;
        }
        catch (error){
            console.error("Falha em carregar os dados dos usuários",error);
        }



    }
    document.addEventListener("DOMContentLoaded", listarUsuarios(0));