function reprovarArquivo(){
    document.querySelectorAll(".reprovar-arquivo").forEach(icon => {
        icon.addEventListener('click', async()=>{
            const fileId = icon.getAttribute('data-id');
            try{
                const response = await fetch(`/reprovar/${fileId}`, {
                    method: 'PUT',
                    credentials: "include"
                })
                if(response.status == 403){
                    mostrarNotificacao("Você não tem permissão para reprovar esse arquivo", "error");
                    return;
                }
                if(!response.ok){
                    throw new Error(`Erro HTTP: ${response.status}`);
                }
                let message = "Arquivo reprovado";
                mostrarNotificacao(message, "alert");
                setTimeout(() => {window.location.reload();

                }, 3000);
            }catch(error){
                let message = "Falha em reprovar arquivo";
                mostrarNotificacao(message, "error");
                console.error("Falha em reprovar arquivo", error);
            }
        });
    });
}

function aprovarArquivo(){
    document.querySelectorAll(".aprovar-arquivo").forEach(icon =>{
        icon.addEventListener('click', async() => {
            const fileId = icon.getAttribute('data-id');
            try{
                const response = await fetch(`/aprovar/${fileId}`, {
                    method: 'PUT',
                    credentials: "include"
                });
                if(response.status == 403){
                    mostrarNotificacao("Você não tem permissão para aprovar esse arquivo", "error");
                    return;
                }
                if(!response.ok){
                    throw new Error(`Erro HTTP: ${response.status}`);
                }
                let message = "Arquivo aprovado com sucesso";
                mostrarNotificacao(message, "success");
                setTimeout(() => {window.location.reload();

                }, 3000);
            }catch(error){
                let message = "Falha em aprovar arquivo";
                mostrarNotificacao(message, "error");
                console.error("Falha em aprovar arquivo", error);
            }
        });
    });
}

function suspenderUsuario(){
    document.querySelectorAll(".botao-suspender-usuario").forEach(button => {
        button.addEventListener('click', async () =>{
            const userId = button.getAttribute('data-id');
            try{
                const response = await fetch(`/suspender/${userId}`, {
                    method: 'PUT',
                    credentials: "include"
                });
                if(!response.ok){
                    throw new Error(`Erro HTTP: ${response.status}`);
                }
                let message = "Usuário suspenso";
                mostrarNotificacao(message, "alert");
                setTimeout(() => {window.location.reload();

                }, 3000);

            }catch(error){
                let message = "Falha em suspender usuario";
                mostrarNotificacao(message, "error");
                console.error("Falha em suspender usuário", error);
            }
        })
    })
}

function retirarSuspensao(){
    document.querySelectorAll(".retirar-suspensao").forEach(button => {
        button.addEventListener('click', async() =>{
            const userId = button.getAttribute('data-id');
            try{
                const response = await fetch(`/retirar-suspensao/${userId}`, {
                    method: 'PUT',
                    credentials: "include"
                });
                if(!response.ok){
                    throw new Error(`Erro HTTP: ${response.status}`);
                }
                let message = "Suspensão do usuário retirada com sucesso";
                mostrarNotificacao(message, "success");
                setTimeout(() => {window.location.reload();

                }, 3000);

            }catch(error){
                let message = "Falha em retirar a suspensão do usuário";
                mostrarNotificacao(message, "error");
                console.error("Falha em retirar a suspensão do usuário", error);
            }
        })
    })
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

async function listarUsuariosComuns(pagina){
    paginaAtual = pagina;
    try{
      const response = await fetch(`/usuarios-comuns?page=${pagina}`, {
        method: 'GET',
        credentials: "include"
      });
      if(!response.ok){
        throw new Error(`Erro HTTP: ${response.status}`);
      }
      const data = await response.json();
      let info = '';
      data.content.forEach(function (usuario){
        info += `<tr><td class="informacoes-perfil"><img src="${usuario.url_foto}">${usuario.nome}</td>
            <td>${usuario.email}</td>
            <td> ${usuario.suspenso === false ? `<button data-id="${usuario.id}" class="botao-suspender-usuario">Suspender usuário</button>` :
                `<button class="retirar-suspensao" data-id="${usuario.id}">Retirar suspensão</button>`}</td></tr>`
      })
      document.getElementById("lista-usuarios-restricao").innerHTML = info;
      retirarSuspensao();
      suspenderUsuario();
      let botoesPagina = '';
      for(let i = 0; i < data.totalPages; i++){
        botoesPagina += `<button onclick="listarUsuariosComuns(${i})" class="${i === paginaAtual ? 'ativa' : ''}">${i+1}</button>`
      }
      document.getElementById("paginacao-restricao").innerHTML = botoesPagina;
    }catch(error){
      console.error("Falha em carregar dados dos usuários", error);
    }
  }

  document.addEventListener("DOMContentLoaded", listarUsuariosComuns(0));

async function listarArquivosPendentes(pagina) {
    paginaAtual = pagina;
    try {
        const response = await fetch(`/solicitacoes?page=${pagina}`, {
            credentials: "include"
        });
        if (!response.ok) {
            throw new Error(`Erro HTTP: ${response.status}`);
        }
        const data = await response.json();
        let info = '';

        if (!data.content || data.content.length === 0) {
            info = `
                <tr>
                    <td colspan="7" style="text-align: center; padding: 20px; color: #666;">
                        Nenhum arquivo pendente no momento.
                    </td>
                </tr>
            `;
        } else {
            data.content.forEach(function (arquivo) {
                const dataArquivo = arquivo.data
                    ? new Date(arquivo.data).toLocaleString("pt-BR")
                    : "-";

                const temDescricao = arquivo.descricao && arquivo.descricao.trim() !== "";
                const descricaoTexto = temDescricao ? arquivo.descricao : "Sem descrição cadastrada pelo autor.";
                const nomeUsuario = arquivo.usuario ? arquivo.usuario.nome : "-";

                info += `
                    <tr class="linha-arquivo" onclick="alternarDescricao(${arquivo.id})" style="cursor: pointer;">
                        <td>
                            <div style="display: flex; align-items: center; gap: 8px;">
                                <span class="material-symbols-outlined icone-seta" id="seta-${arquivo.id}" style="font-size: 18px; color: #666; transition: transform 0.2s;">
                                    chevron_right
                                </span>
                                <span>${arquivo.nome}</span>
                            </div>
                        </td>
                        <td>${arquivo.disciplina}</td>
                        <td>${dataArquivo}</td>
                        <td>${nomeUsuario}</td>
                        <td onclick="event.stopPropagation()">
                            <span class="material-symbols-outlined botao-visualizar" data-id="${arquivo.id}" title="Visualizar">
                                visibility
                            </span>
                        </td>
                        <td onclick="event.stopPropagation()">
                            <button class="aprovar-arquivo" data-id="${arquivo.id}">Aprovar</button>
                        </td>
                        <td onclick="event.stopPropagation()">
                            <button class="reprovar-arquivo" data-id="${arquivo.id}">Reprovar</button>
                        </td>
                    </tr>
                    <tr id="descricao-${arquivo.id}" class="linha-descricao" style="display: none; background-color: #f9fbfd;">
                        <td colspan="7" style="padding: 12px 24px; border-bottom: 1px solid #e0e0e0;">
                            <p style="margin: 0; color: #4a5568; font-size: 0.9em; line-height: 1.4;">
                                <strong style="color: #2d3748;">Descrição do Envio:</strong> ${descricaoTexto}
                            </p>
                        </td>
                    </tr>
                `;
            });
        }

        document.getElementById("lista-arquivos-pendentes").innerHTML = info;
        visualizarArquivo();
        aprovarArquivo();
        reprovarArquivo();

        let botoesPagina = '';
        for (let i = 0; i < data.totalPages; i++) {
            botoesPagina += `<button onclick="listarArquivosPendentes(${i})" class="${i === paginaAtual ? 'ativa' : ''}">${i + 1}</button>`;
        }
        document.getElementById("paginacao-solicitacoes").innerHTML = botoesPagina;

    } catch (error) {
        console.error("Falha em carregar arquivos", error);
    }
}

document.addEventListener("DOMContentLoaded",  listarArquivosPendentes(0));
