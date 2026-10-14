function deletarArquivo(){
    document.querySelectorAll(".deletar-arquivo").forEach(icon => {
        icon.addEventListener('click', async() =>{
            const fileId = icon.getAttribute('data-id');
            try{
                const response = await fetch(`/deletar/${fileId}`,{
                    method:'DELETE',
                    credentials: "include"
                });
                if(!response.ok){
                    throw new Error(`Erro HTTP: ${response.status}`);
                }
                let message = "Arquivo deletado";
                mostrarNotificacao(message, "alert");
                setTimeout(() => {window.location.reload();

                }, 3000);
            }catch(error){
                let message = "Falha em deletar arquivo";
                mostrarNotificacao(message, "error")
                console.error("Falha em deletar arquivo", error);
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

async function listarArquivosUsuario(pagina) {
    paginaAtual = pagina;
    try {
        const response = await fetch(`/envios?page=${pagina}`, {
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
                    <td colspan="6" style="text-align: center; padding: 20px; color: #666;">
                        Nenhum arquivo enviado no momento. Mude isso contribuindo com o repositório!
                    </td>
                </tr>
            `;
        } else {
            data.content.forEach(function (arquivo) {
                const reprovado = arquivo.status === "REPROVADO";
                const temDescricao = arquivo.descricao && arquivo.descricao.trim() !== "";
                const descricaoTexto = temDescricao ? arquivo.descricao : "Sem descrição cadastrada.";

                const acoes = reprovado
                    ? `
                        <td>—</td>
                        <td>—</td>
                        <td>—</td>
                    `
                    : `
                        <td onclick="event.stopPropagation()">
                            <span class="material-symbols-outlined botao-visualizar"
                                data-id="${arquivo.id}" title="Visualizar">
                                visibility
                            </span>
                        </td>
                        <td onclick="event.stopPropagation()">
                            <span class="material-symbols-outlined botao-download"
                                data-id="${arquivo.id}"
                                data-nome="${arquivo.nome_real_arquivo}" title="Download">
                                download
                            </span>
                        </td>
                        <td onclick="event.stopPropagation()">
                            <span class="material-symbols-outlined deletar-arquivo"
                                data-id="${arquivo.id}" title="Deletar">
                                delete
                            </span>
                        </td>
                    `;

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
                        <td>${arquivo.status}</td>
                        ${acoes}
                    </tr>
                    <tr id="descricao-${arquivo.id}" class="linha-descricao" style="display: none; background-color: #f9fbfd;">
                        <td colspan="6" style="padding: 12px 24px; border-bottom: 1px solid #e0e0e0;">
                            <p style="margin: 0; color: #4a5568; font-size: 0.9em; line-height: 1.4;">
                                <strong style="color: #2d3748;">Descrição:</strong> ${descricaoTexto}
                            </p>
                        </td>
                    </tr>
                `;
            });
        }

        document.getElementById("lista-arquivos-usuario").innerHTML = info;
        downloadArquivo();
        visualizarArquivo();
        deletarArquivo();

        let botoesPaginas = '';
        for (let i = 0; i < data.totalPages; i++) {
            botoesPaginas += `<button onclick="listarArquivosUsuario(${i})" class="${i === paginaAtual ? 'ativa' : ''}">${i + 1}</button>`;
        }
        document.getElementById("paginacao-arquivos-usuario").innerHTML = botoesPaginas;

    } catch (error) {
        console.error("Falha em carregar arquivos do usuário", error);
    }
}
document.addEventListener("DOMContentLoaded", listarArquivosUsuario(0));