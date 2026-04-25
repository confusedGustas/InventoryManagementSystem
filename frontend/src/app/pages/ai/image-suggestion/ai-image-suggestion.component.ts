import {Component, input, output, signal} from '@angular/core';

@Component({
    selector: 'app-ai-image-suggestion',
    standalone: true,
    templateUrl: './ai-image-suggestion.component.html'
})
export class AiImageSuggestionComponent {

    readonly loading = input.required<boolean>();
    readonly scopeLabel = input.required<string>();

    readonly imageSelected = output<File>();

    protected readonly fileName = signal<string | null>(null);

    protected onFileChange(event: Event): void {
        const input = event.target as HTMLInputElement;
        const file = input.files?.[0];
        if (!file) {
            return;
        }

        this.fileName.set(file.name);
        this.imageSelected.emit(file);
        input.value = '';
    }

}
