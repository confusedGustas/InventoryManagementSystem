import {DatePipe} from '@angular/common';
import {Component, effect, inject, input, output} from '@angular/core';
import {FormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {ApiKeyDto, SettingsService} from '../settings.service';
import {ToastService} from '../../../shared/toast/toast.service';

@Component({
    selector: 'app-api-key',
    standalone: true,
    imports: [ReactiveFormsModule, DatePipe],
    templateUrl: './api-key.component.html'
})
export class ApiKeyComponent {

    private readonly settingsService = inject(SettingsService);
    private readonly formBuilder = inject(FormBuilder);
    private readonly toast = inject(ToastService);

    readonly apiKey = input.required<string>();
    readonly companyName = input.required<string | null>();
    readonly updatedOn = input.required<string | null>();
    readonly apiKeySaved = output<ApiKeyDto>();

    protected readonly form = this.formBuilder.nonNullable.group({
        apiKey: ['', Validators.required],
    });

    constructor() {
        effect(() => {
            this.form.patchValue({ apiKey: this.apiKey() ?? '' }, { emitEvent: false });
        });
    }

    protected submit(): void {
        if (this.form.invalid) {
            this.form.markAllAsTouched();
            this.toast.error('API key is required.');
            return;
        }

        this.settingsService.saveCompanyApiKey(this.form.getRawValue()).subscribe({
            next: (savedApiKey) => {
                this.form.patchValue({ apiKey: savedApiKey.apiKey }, { emitEvent: false });
                this.apiKeySaved.emit(savedApiKey);
                this.toast.success('API key saved successfully.');
            },
            error: () => {
                this.toast.error('Failed to save API key. Please try again.');
            }
        });
    }

}
